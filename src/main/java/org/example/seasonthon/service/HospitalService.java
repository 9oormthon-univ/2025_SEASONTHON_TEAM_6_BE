package org.example.seasonthon.service;


import lombok.RequiredArgsConstructor;
import org.example.seasonthon.dto.HospitalLocResponseDto;
import org.example.seasonthon.dto.HospitalMapResponseDto;
import org.example.seasonthon.dto.SymptomResponseDto;
import org.example.seasonthon.entity.Hospital;
import org.example.seasonthon.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class HospitalService {
  // 영어/언어명/약어 → 한글 태그 매핑 테이블 (키는 소문자 기준)
  private static final Map<String, String> EN_TO_KO = Map.ofEntries(
          entry("Mongolia", "몽골"),
          entry("America", "미국"),
          entry("Vietnam", "베트남"),
          entry("Russia", "러시아"),
          entry("Japan", "일본"),
          entry("China", "중국"),
          entry("Middle East", "중동"),
          entry("Korea", "한국"),
          entry("Other", "기타")
  );

  private final HospitalRepository hospitalRepository;
  private final SymptomService symptomService;


//  public List<HospitalLocResponseDto> findHospitalsNearby(double userLat, double userLng, String prompt, String country) {
//    // 🔍 1. 증상으로부터 진료과목 & 언어 추론
//    SymptomResponseDto symptomInfo = symptomService.processSymptom(prompt);
//
//    String department = symptomInfo.getDepartment(); // 사용자가 진료를 볼 진료과목
//
//    // 🔍 2. 모든 병원 조회
//    List<Hospital> allHospitals = hospitalRepository.findAll();
//
//    // 🔍 3. 필터링: 반경 1km 이내, 진료과목 포함, 언어 일치
//    return allHospitals.stream()
//            // 1. 반경 1km 이내에
//            .filter(h -> distance(userLat, userLng, h.getLatitude(), h.getLongitude()) < 1.0)
//            // 2. 사용자가 진료 볼 진료과목을 포함하고
//            //.filter(h -> h.getSpecialtyKorean() != null && h.getSpecialtyKorean().contains(department))
//            .filter(h -> hasExactDepartment(h.getSpecialtyKorean(), department))
//
//            // 🔎 언어 필터 직전, 현재 튜플(병원)의 언어 출력
//            .peek(h -> {
//              String koTarget = EN_TO_KO.get(country); // 사용자가 고른 나라(영→한 매핑)
//              String langs = String.valueOf(h.getLanguage()); // null 안전하게 문자열화
//              System.out.println(String.format(
//                      "언어 필터 전 | 병원='%s', 언어='%s', 찾는국가(ko)='%s'",
//                      h.getNameKorean(), langs, koTarget
//              ));
//            })
//            // 3. 사용자가 선택한 나라를 포함하는
//            .filter(h -> h.getLanguage() != null && h.getLanguage().contains(EN_TO_KO.get(country)))
//            // 4. 의료기관 검색
//            .map(h -> new HospitalLocResponseDto(
//                    h.getNameKorean(),
//                    h.getAddressKorean(),
//                    h.getSpecialtyKorean(),
//                    h.getPNumber(),
//                    h.getLanguage(),
//                    h.getLatitude(),
//                    h.getLongitude(),
//                    h.getYkiho()
//            ))
//            .collect(Collectors.toList());
//  }

  public HospitalMapResponseDto findHospitalsNearby(double userLat, double userLng, String prompt, String country) {
    // 1. 증상으로부터 진료과목 추론
    SymptomResponseDto symptomInfo = symptomService.processSymptom(prompt);
    String department = symptomInfo.getDepartment();

    // 2. 모든 병원 조회
    List<Hospital> allHospitals = hospitalRepository.findAll();

    // 3. 조건에 맞는 병원 필터링
    List<HospitalLocResponseDto> filteredHospitals = allHospitals.stream()
            .filter(h -> distance(userLat, userLng, h.getLatitude(), h.getLongitude()) < 1.0)
            .filter(h -> hasExactDepartment(h.getSpecialtyKorean(), department))
            .filter(h -> h.getLanguage() != null && h.getLanguage().contains(EN_TO_KO.get(country)))
            .map(h -> new HospitalLocResponseDto(
                    h.getNameKorean(),
                    h.getAddressKorean(),
                    h.getSpecialtyKorean(),
                    h.getPNumber(),
                    h.getLanguage(),
                    h.getLatitude(),
                    h.getLongitude(),
                    h.getYkiho()
            ))
            .collect(Collectors.toList());

//    // 4. Static Maps API URL 생성
//    String markers = filteredHospitals.stream()
//            .map(h -> "&markers=color:red|" + h.latitude() + "," + h.longitude())
//            .collect(Collectors.joining());

    // 4. Static Maps API URL 생성
    String markers = filteredHospitals.stream()
            .map(h -> "&markers=icon:https://maps.gstatic.com/mapfiles/ms2/micons/hospitals.png|"
                    + h.latitude() + "," + h.longitude())
            .collect(Collectors.joining());

    String mapUrl = String.format(
            "https://maps.googleapis.com/maps/api/staticmap?size=600x400&zoom=14%s&key=%s",
            markers,
            System.getenv("GOOGLE_MAPS_API_KEY") // 환경변수에서 키 불러오기
    );

    // 5. 병원 리스트와 지도 URL 함께 반환
    return new HospitalMapResponseDto(mapUrl, filteredHospitals);
  }

  private boolean hasExactDepartment(String specialties, String dept) {
    if (specialties == null || dept == null) return false;
    // "구강악안면외과, 치과보철과, ..., 구강내과, ..., 예방치과" 형태를 쉼표로 분리해 완전 일치만 검사
    return Arrays.stream(specialties.split("\\s*,\\s*"))
            .map(String::trim)
            .anyMatch(dept::equals);
  }

  /**
   * 두 지점(위도, 경도) 사이의 거리(km)를 계산하는 메서드
   * Haversine 공식을 사용하여 구면 거리(Great-circle distance)를 구함
   *
   * @param lat1 사용자 위도
   * @param lon1 사용자 경도
   * @param lat2 병원 위도
   * @param lon2 병원 경도
   * @return 두 위치 사이의 거리 (단위: km)
   */
  private double distance(double lat1, double lon1, double lat2, double lon2) {
    // 지구 반지름 (단위: km)
    double R = 6371;

    // 위도, 경도 차이를 라디안 단위로 변환
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lon2 - lon1);

    // Haversine 공식 적용
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);

    // 최종 거리 계산 (단위: km)
    return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
  }
}

package org.example.seasonthon.dto;

// 현재 사용자의 위치정보를 기준으로, 주변 5km 반경, 외국인 진료 가능 병원목록 반환
public record HospitalLocResponseDto(
        String name_korean,                // 병원명 국문
//        String name_english,               // 병원명 영문

        String address_korean,             // 주소 국문
//        String address_english,            // 주소 영문

        String specialty_korean,           // 진료 과목 국문
//        String specialty_english,          // 진료 과목 영문

        String p_number,                   // 전화번호
        String language,                   // 지원 가능 언어

        Double latitude,                   // 위도
        Double longitude,                   // 경도

        String ykiho                       // HIRA, 의료기관 식별 코드
) {}

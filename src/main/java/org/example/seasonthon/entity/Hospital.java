package org.example.seasonthon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Hospital {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nameKorean;                  // 병원명 국문
//  private String nameEnglish;               // 병원명 영문

  private String addressKorean;               // 주소 국문
//  private String addressEnglish;            // 주소 영문

  private String specialtyKorean;             // 진료 과목 국문
//  private String specialtyEnglish;          // 진료 과목 영문

  private String pNumber;                     // 전화번호
  private String language;                    // 지원 가능 언어

  private Double latitude;                    // 위도
  private Double longitude;                   // 경도

  private String ykiho;                       // HIRA, 의료기관 식별 코드
}


//INSERT INTO hospital (name_korean, p_number, language, address_korean, longitude, latitude, ykiho, specialty_korean) VALUES ('서울베스트비뇨의학과의원', '02-6958-7532', '중국', '서울특별시 강남구 봉은사로 230, 봉암빌딩 5층 501호 (역삼동)', 127.0375393, 37.5081265, 'JDQ4MTg4MSM1MSMkMiMkOCMkMDAkMzgxOTYxIzQxIyQxIyQ3IyQxMyQzNjE4MzIjNDEjJDEjJDgjJDgz', '비뇨의학과');

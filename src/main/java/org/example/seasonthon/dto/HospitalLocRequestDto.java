package org.example.seasonthon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

// 현재 사용자의 위치 전달
public record HospitalLocRequestDto(

//        @Schema(description = "현재 사용자의 위도", example = "37.49877474832768")
//        Double latitude,    // 위도
//
//        @Schema(description = "현재 사용자의 경도", example = "127.0278195920194")
//        Double longitude,   // 경도

        @Schema(description = "현재 사용자가 입력한 증상", example = "My teeth hurt")
        String prompt,    // 사용자의 증상

        @Schema(description = "현재 사용자의 국가", example = "America")
        String country  // 사용자 국가

) {}
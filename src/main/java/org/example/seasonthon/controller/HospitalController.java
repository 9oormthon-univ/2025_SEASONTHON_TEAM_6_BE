package org.example.seasonthon.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.seasonthon.dto.HospitalLocRequestDto;
import org.example.seasonthon.dto.HospitalLocResponseDto;
import org.example.seasonthon.dto.HospitalMapResponseDto;
import org.example.seasonthon.service.HospitalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hospital")

@Tag(name = "증상 판별 + 병원 검색 API", description = "증상 판별과 사용자 위치 기반으로 병원을 검색합니다.")
public class HospitalController {

  private final HospitalService hospitalService;

  @PostMapping("/search")
  @Operation(
          description = "사용자의 위도, 경도, 언어 정보, 진료과목을 바탕으로 반경 1km 내의 병원 정보를 반환합니다."
  )
  public HospitalMapResponseDto getNearbyHospitals(
          @RequestBody HospitalLocRequestDto dto
  ) {

    HospitalMapResponseDto ret = hospitalService.findHospitalsNearby(37.49877474832768, 127.0278195920194, dto.prompt(), dto.country());
    return ret;
  }
}

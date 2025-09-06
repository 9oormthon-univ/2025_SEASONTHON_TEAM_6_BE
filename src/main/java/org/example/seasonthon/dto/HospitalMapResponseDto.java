package org.example.seasonthon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HospitalMapResponseDto {
  private String mapUrl; // Static Maps API URL
  private List<HospitalLocResponseDto> hospitals; // 병원 리스트
}
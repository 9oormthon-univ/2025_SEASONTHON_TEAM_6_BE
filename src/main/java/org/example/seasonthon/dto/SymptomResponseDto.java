package org.example.seasonthon.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SymptomResponseDto {
  private String department;      // 사용자 증상에 해당하는 진료과목
}

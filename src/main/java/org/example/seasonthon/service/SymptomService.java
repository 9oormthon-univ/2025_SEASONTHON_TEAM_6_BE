package org.example.seasonthon.service;


import lombok.RequiredArgsConstructor;
import org.example.seasonthon.dto.AiPredictionResult;
import org.example.seasonthon.dto.SymptomResponseDto;
import org.springframework.stereotype.Service;
import org.example.seasonthon.config.AiClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymptomService {

  private final AiClient aiClient;

  public SymptomResponseDto processSymptom(String prompt) {
    // ✅ AI를 통해 진료과 + 언어 추론
    AiPredictionResult prediction = aiClient.analyzePrompt(prompt);

    // ⛔ openApiClient로 languages 매핑 생략 가능 (AI가 직접 추론하므로)

    System.out.println("SymptomService에서 예측된 진료과: ");
    System.out.println(prediction.getDepartment());

    return SymptomResponseDto.builder()
            .department(prediction.getDepartment())
            .build();
  }
}

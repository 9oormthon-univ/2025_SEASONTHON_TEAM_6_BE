package org.example.seasonthon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.seasonthon.dto.AiPredictionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiClient {

  // ✅ OpenAI API Key 를 .env (.yml or .properties) 에서 주입받음
  @Value("${openai.key}")
  private String openAiKey;

  // ✅ SpringBoot 가 RestTemplate 빈을 주입해줌
  private final RestTemplate restTemplate;

  /**
   * 사용자의 증상 프롬프트를 기반으로 진료과와 언어를 추론
   */
  public AiPredictionResult analyzePrompt(String prompt) {

    // ✅ 시스템 프롬프트 (명확하게 JSON 포맷 + 허용된 리스트 제한)
    String systemPrompt = """
              당신은 의료 분류 보조 모델입니다.
              사용자의 입력(증상)을 바탕으로 아래 값을 **JSON만** 출력하세요.
            
              출력 형식 (오직 이 JSON만 출력; 다른 텍스트/설명/마크다운 금지)
              {
                "department": "진료과목"
              }
            
              목표
              - 병원에서 진료받아야 할 진료과목 (department) 판별
            
              허용 값(반드시 아래 목록 중 하나만 사용)
              [내과, 마취통증의학과, 성형외과, 소아과, 안과, 영상의학과, 외과, 이비인후과, 재활의학과, 정신과, 치과, 피부과, 한의학]
            
              진료과 결정 가이드
              - 감기·발열·기침·흉통·복통·소화기 질환·만성질환 관리 → 내과
              - 소아·청소년 내과적 문제 → 소아청소년과 (치아 문제면 소아치과)
              - 코·목·귀 → 이비인후과
              - 눈 → 안과
              - 피부 발진·여드름·가려움 → 피부과
              - 심한 두통·어지럼·경련·마비 → 신경과
              - 우울·불안·공황·환청 → 정신건강의학과
              - 골절·관절·근육·인대 손상 → 정형외과
              - 재활·기능 회복 → 재활의학과
              - 외상 봉합·수술적 처치 필요 → 외과
              - 임신·생리·여성 생식기 → 산부인과
              - 치과 문제:
                - 충치·치통 → 치과보존과
                - 잇몸·치주 → 치주과
                - 사랑니·턱 외과적 문제 → 구강악안면외과
                - 교정 → 치과교정과
                - 보철 → 치과보철과
                - 원인 불명·포괄 → 통합치의학과
              - 미용·외형 교정 → 성형외과
              - 만성 통증 관리 → 마취통증의학과
              - 한방 진료를 원하는 경우만 해당 한방과
              - 분류 불가·모호할 경우 → 가정의학과 또는 일반의
            
              출력은 항상 아래 JSON 형식으로만:
              {"department":"..."}
            """;

    // ===================================================================================================
    // ✅ 요청 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(openAiKey); // Authorization: Bearer {key}
    headers.setContentType(MediaType.APPLICATION_JSON);

    // ✅ 요청 바디 구성 (ChatGPT 메시지 구조)
    Map<String, Object> body = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", prompt)
            )
    );

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    String url = "https://api.openai.com/v1/chat/completions";
    // ===================================================================================================

    try {
      // ✅ API 요청 전송 (POST)
      ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

      Map<?, ?> responseBody = response.getBody();
      List<?> choices = (List<?>) responseBody.get("choices");
      Map<?, ?> choice0 = (Map<?, ?>) choices.get(0);
      Map<?, ?> message = (Map<?, ?>) choice0.get("message");

      String json = (String) message.get("content");

      System.out.println("가공된 json: \n" + json);

      ObjectMapper mapper = new ObjectMapper();
      AiPredictionResult result = mapper.readValue(json, AiPredictionResult.class);

      return result;

    } catch (Exception e) {
      // 예외 처리: 추론 실패
      throw new RuntimeException("AI 진료과 및 언어 추론 실패", e);
    }
  }
}
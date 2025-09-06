import os
import json
from openai import OpenAI
from dotenv import load_dotenv

load_dotenv()

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

SYSTEM_PROMPT = """
너는 한국의 1차 진료 네비게이터야.

사용자가 한국어로 자연스럽게 증상을 묘사하면,
가장 적절한 진료과를 1개 이상 추천해줘.

출력 형식은 아래 JSON 형태만 사용하고, 다른 문장은 절대 쓰지 마.

형식:
{
  "departments": ["추천진료과1", "추천진료과2", "..."]
}

규칙:
- 반드시 아래 목록 중에서만 진료과를 선택해야 해.
- 만약 증상이 애매하거나 특정 진료과로 단정짓기 어렵다면 "가정의학과" 또는 "내과"를 포함해.
- 설명 문장 없이 JSON만 출력할 것.

허용된 진료과 목록:
응급의학과, 이비인후과, 예방의학과, 재활의학과, 정신건강의학과, 진단검사의학과, 직업환경의학과, 피부과, 핵의학과, 치과,
산부인과, 소아청소년과, 신경과, 안과, 영상의학과, 외과, 마취통증의학과, 비뇨의학과, 방사선종양학과, 병리과,
가정의학과, 결핵과, 내과
"""


def get_departments_by_symptom(symptom: str) -> list[str]:
    """
    사용자의 증상 설명을 바탕으로 ChatGPT에게 진료과 추천 요청.
    :param symptom: 증상 텍스트 (한국어 자유서술)
    :return: 추천된 진료과 리스트
    """
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        temperature=0.2,
        response_format={"type": "json_object"},
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": symptom},
        ],
    )
    parsed = json.loads(response.choices[0].message.content)
    return parsed["departments"]

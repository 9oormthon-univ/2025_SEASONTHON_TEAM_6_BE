import os
from dotenv import load_dotenv
from urllib.parse import urlencode
import httpx
import jwt

load_dotenv()

GOOGLE_CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID")
REDIRECT_URI = os.getenv("REDIRECT_URI")
GOOGLE_CLIENT_SECRET = os.getenv("GOOGLE_CLIENT_SECRET")
GOOGLE_CALENDAR_API = "https://www.googleapis.com/calendar/v3/users/me/calendarList"

if not GOOGLE_CLIENT_ID or not REDIRECT_URI:
    raise ValueError("환경변수 GOOGLE_CLIENT_ID 또는 REDIRECT_URI가 누락되었습니다.")


def generate_google_oauth_url():
    base_url = "https://accounts.google.com/o/oauth2/v2/auth"

    # scope는 공백으로 구분
    scopes = [
        "openid",
        "email",
        "profile",
        "https://www.googleapis.com/auth/calendar",  # 캘린더 권한 포함
    ]

    params = {
        "client_id": GOOGLE_CLIENT_ID,
        "redirect_uri": REDIRECT_URI,
        "response_type": "code",
        "scope": " ".join(scopes),
        "access_type": "offline",
        "prompt": "consent",  # 매번 refresh_token 받도록
        "include_granted_scopes": "true",  # 기존 동의 스코프 유지
    }

    url = f"{base_url}?{urlencode(params)}"
    print("생성된 구글 OAuth URL:", url)
    return url


# code → access_token + id_token 교환
async def exchange_code_for_token(code: str) -> dict:
    token_url = "https://oauth2.googleapis.com/token"
    data = {
        "code": code,
        "client_id": GOOGLE_CLIENT_ID,
        "client_secret": GOOGLE_CLIENT_SECRET,
        "redirect_uri": REDIRECT_URI,
        "grant_type": "authorization_code",
    }

    async with httpx.AsyncClient() as client:
        response = await client.post(token_url, data=data)
        response.raise_for_status()
        return response.json()


# 인가코드가지고 개인정보 decoding 하기
def decode_id_token(id_token: str) -> dict:
    decoded = jwt.decode(id_token, options={"verify_signature": False})
    return {
        "email": decoded.get("email"),
        "name": decoded.get("name"),
        "picture": decoded.get("picture"),
        "sub": decoded.get("sub"),  # Google 고유 사용자 ID
    }


# token 가지고 google calender api호출
async def get_user_calendars(access_token: str) -> dict:
    headers = {"Authorization": f"Bearer {access_token}"}
    async with httpx.AsyncClient() as client:
        response = await client.get(GOOGLE_CALENDAR_API, headers=headers)
        response.raise_for_status()
        return response.json()

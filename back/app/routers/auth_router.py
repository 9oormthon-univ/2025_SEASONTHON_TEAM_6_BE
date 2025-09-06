from fastapi import APIRouter, Request
from fastapi.responses import RedirectResponse
from urllib.parse import urlencode
from app.services.auth_service import (
    generate_google_oauth_url,
    exchange_code_for_token,
    get_user_calendars,
    decode_id_token,
)
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)

router = APIRouter(prefix="/login/oauth2/code", tags=["auth"])


# 로그인 시작 (Google 로그인 창으로 이동)
@router.get("/login/google")
def google_login():
    oauth_url = generate_google_oauth_url()
    return RedirectResponse(oauth_url)


# 로그인 완료 후 구글이 리디렉션해주는 콜백
@router.get("/google")
async def handle_google_oauth_callback(request: Request):
    logging.info("--- Google OAuth Callback Start ---")

    # 1. 인가 코드 받기
    code = request.query_params.get("code")
    logging.info(
        f"Step 1: Received authorization code: {code[:20] if code else 'None'}..."
    )
    if not code:
        logging.error("Error: No authorization code provided.")
        return {"error": "No authorization code provided"}

    try:
        # 2. 인가 코드로 토큰 교환
        logging.info("Step 2: Exchanging code for token...")
        token_data = await exchange_code_for_token(code)
        logging.info(f"Step 2: Received token data: {token_data}")

        access_token = token_data.get("access_token")
        id_token = token_data.get("id_token")

        if not access_token or not id_token:
            logging.error(
                "Error: Failed to retrieve access_token or id_token from token_data."
            )
            return {"error": "Failed to retrieve tokens"}

        logging.info(f"Step 2: Extracted access_token: {access_token[:20]}...")
        logging.info(f"Step 2: Extracted id_token: {id_token[:20]}...")

    except Exception as e:
        logging.error(f"Error in Step 2 (Token Exchange): {e}", exc_info=True)
        return {"error": "Failed to exchange code for token", "details": str(e)}

    try:
        # 3. 사용자 정보 디코딩
        logging.info("Step 3: Decoding ID token...")
        user_info = decode_id_token(id_token)
        logging.info(f"Step 3: Decoded user info: {user_info}")

    except Exception as e:
        logging.error(f"Error in Step 3 (Decode ID Token): {e}", exc_info=True)
        return {"error": "Failed to decode ID token", "details": str(e)}

    try:
        # 4. 캘린더 정보 가져오기
        logging.info("Step 4: Fetching user calendars...")
        calendar_list = await get_user_calendars(access_token)
        logging.info(f"Step 4: Fetched calendar list: {calendar_list}")

    except Exception as e:
        logging.error(f"Error in Step 4 (Fetch Calendars): {e}", exc_info=True)
        return {"error": "Failed to fetch user calendars", "details": str(e)}

    logging.info("--- Google OAuth Callback End ---")
    query_params = urlencode(
        {
            "access_token": access_token,
            "email": user_info.get("email"),
            "name": user_info.get("name"),
        }
    )

    redirect_url = f"https://2025-seasonthon-team-6-fe.vercel.app/?{query_params}"

    return RedirectResponse(url=redirect_url)

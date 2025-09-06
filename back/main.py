from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from app.routers import dpm_router
from app.routers import auth_router
from app.routers import calender_router

load_dotenv()

app = FastAPI(title="구름톤 FastAPI 백엔드")

# CORS 미들웨어 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://2025-seasonthon-team-6-fe.vercel.app/"],
    allow_credentials=False,  # credentials를 False로
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["*"],
)
# 라우터 등록
app.include_router(dpm_router.router)
app.include_router(auth_router.router)
app.include_router(calender_router.router)

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from app.routers import dpm_router
from app.routers import auth_router

load_dotenv()

app = FastAPI(title="구름톤 FastAPI 백엔드")

# CORS 미들웨어 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"]  # 모든 오리진 허용
)

# 라우터 등록
app.include_router(dpm_router.router)
app.include_router(auth_router.router)

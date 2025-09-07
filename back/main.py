from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from app.routers import dpm_router
from app.routers import auth_router
from app.routers import calender_router


load_dotenv()

app = FastAPI(title="구름톤 FastAPI 백엔드")

# CORS 미들웨어 추가
origins = [
    "http://localhost:3000",
    "https://2025-seasonthon-team-6-fe.vercel.app",  # 프론트엔드 주소
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],  # 👈 OPTIONS를 포함한 모든 메소드를 허용하는지 확인!
    allow_headers=[
        "*"
    ],  # 👈 Content-Type, Authorization 등 모든 헤더를 허용하는지 확인!
)
# 라우터 등록
app.include_router(dpm_router.router)
app.include_router(auth_router.router)
app.include_router(calender_router.router)

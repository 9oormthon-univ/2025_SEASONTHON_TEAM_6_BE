# app/routers/calendar_router.py

from fastapi import APIRouter, Body
from app.services.calender_service import create_google_calendar_event

router = APIRouter(prefix="/calendar", tags=["calendar"])


@router.post("/add_event")
async def add_event(
    access_token: str = Body(...),
    summary: str = Body(...),
    start_time: str = Body(...),
    end_time: str = Body(...),
):
    result = await create_google_calendar_event(
        access_token=access_token,
        summary=summary,
        start_time=start_time,
        end_time=end_time,
    )
    return result

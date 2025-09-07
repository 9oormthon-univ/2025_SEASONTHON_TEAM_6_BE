from fastapi import APIRouter, Body
from typing import Optional
from app.services.calendar_service import (
    create_google_calendar_event,
    list_google_calendar_events,
)

router = APIRouter(prefix="/calendar", tags=["calendar"])


@router.post("/add_event")
async def add_event(
    access_token: str = Body(...),
    summary: str = Body(...),
    start_time: str = Body(...),  # RFC3339
    end_time: str = Body(...),  # RFC3339
):
    result = await create_google_calendar_event(
        access_token=access_token,
        summary=summary,
        start_time=start_time,
        end_time=end_time,
    )
    return result


@router.post("/list_events")
async def list_events(
    access_token: str = Body(...),
    calendar_id: str = Body(default="primary"),
    time_min: Optional[str] = Body(default=None),  # RFC3339
    time_max: Optional[str] = Body(default=None),  # RFC3339
    max_results: int = Body(default=50),
    single_events: bool = Body(default=True),
    order_by: Optional[str] = Body(default="startTime"),  # or "updated"
):
    result = await list_google_calendar_events(
        access_token=access_token,
        calendar_id=calendar_id,
        time_min=time_min,
        time_max=time_max,
        max_results=max_results,
        single_events=single_events,
        order_by=order_by,
    )
    return {
        "calendar_id": calendar_id,
        "events": result["events"],
        "nextPageToken": result.get("nextPageToken"),
    }

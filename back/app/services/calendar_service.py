import httpx
import logging
from typing import Optional, Dict, Any

GOOGLE_CALENDAR_CREATE_API = (
    "https://www.googleapis.com/calendar/v3/calendars/primary/events"
)
GOOGLE_CALENDAR_LIST_API_BASE = "https://www.googleapis.com/calendar/v3"


# 일정 추가
async def create_google_calendar_event(
    access_token: str, summary: str, start_time: str, end_time: str
):
    headers = {
        "Authorization": f"Bearer {access_token}",
        "Content-Type": "application/json",
    }

    event_data = {
        "summary": summary,
        "start": {"dateTime": start_time, "timeZone": "Asia/Seoul"},
        "end": {"dateTime": end_time, "timeZone": "Asia/Seoul"},
    }

    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                GOOGLE_CALENDAR_CREATE_API, headers=headers, json=event_data
            )
            response.raise_for_status()
            return response.json()
    except httpx.HTTPStatusError as e:
        logging.error(f"[Google Calendar API Error] {e.response.text}")
        return {"error": "Google Calendar API error", "details": e.response.text}


# 일정 목록 조회
async def list_google_calendar_events(
    access_token: str,
    calendar_id: str = "primary",
    time_min: Optional[str] = None,
    time_max: Optional[str] = None,
    max_results: int = 50,
    page_token: Optional[str] = None,
    single_events: bool = True,
    order_by: Optional[str] = "startTime",
) -> Dict[str, Any]:
    url = f"{GOOGLE_CALENDAR_LIST_API_BASE}/calendars/{calendar_id}/events"
    headers = {"Authorization": f"Bearer {access_token}"}
    params: Dict[str, Any] = {
        "maxResults": max_results,
        "singleEvents": str(single_events).lower(),
    }
    if order_by:
        params["orderBy"] = order_by
    if time_min:
        params["timeMin"] = time_min
    if time_max:
        params["timeMax"] = time_max
    if page_token:
        params["pageToken"] = page_token

    async with httpx.AsyncClient(timeout=20.0) as client:
        r = await client.get(url, headers=headers, params=params)
        r.raise_for_status()
        data = r.json()
        return {
            "events": data.get("items", []),
            "nextPageToken": data.get("nextPageToken"),
            "raw": data,  # 필요 시 디버깅용
        }

import httpx
import logging

GOOGLE_CALENDAR_CREATE_API = (
    "https://www.googleapis.com/calendar/v3/calendars/primary/events"
)


async def create_google_calendar_event(
    access_token: str, summary: str, description: str, start_time: str, end_time: str
):
    headers = {
        "Authorization": f"Bearer {access_token}",
        "Content-Type": "application/json",
    }

    event_data = {
        "summary": summary,
        "description": description,
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

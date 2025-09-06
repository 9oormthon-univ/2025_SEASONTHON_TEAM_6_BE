from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.services.dpm_service import get_departments_by_symptom

router = APIRouter()


class SymptomRequest(BaseModel):
    symptom: str


class SymptomResponse(BaseModel):
    departments: list[str]


@router.post("/suggest-department", response_model=SymptomResponse)
def suggest_department(req: SymptomRequest):
    try:
        departments = get_departments_by_symptom(req.symptom)
        return {"departments": departments}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

from fastapi import APIRouter, Depends, HTTPException, Body, UploadFile, File, Form
from typing import Optional
from datetime import datetime
from app.models.event import EventResponse, EventType, ThreatLevel, EventCreate
from app.database.repositories.event_repository import EventRepository
from app.dependencies import get_event_repository
import os
import uuid
import aiofiles

router = APIRouter(prefix="/api/demo", tags=["Demo Mode"])

@router.post("/trigger", status_code=201)
async def trigger_demo_event(
    type: str = Form(...),
    description: Optional[str] = Form(None),
    is_threat: bool = Form(False),
    threat_confidence: Optional[str] = Form("LOW"),
    threat_explanation: Optional[str] = Form(None),
    matched_face_id: Optional[int] = Form(None),
    image: Optional[UploadFile] = File(None),
    repo: EventRepository = Depends(get_event_repository)
):
    """
    Manually trigger a mock AI event for demo purposes.
    Supports optional image upload which will be saved as the event screenshot.
    """
    try:
        event_type = EventType(type)
    except ValueError:
        raise HTTPException(status_code=400, detail=f"Invalid event type: {type}")

    # Default descriptions if none provided
    if not description:
        descriptions = {
            EventType.MOTION: "Significant motion detected at front porch",
            EventType.PERSON_DETECTED: "Unrecognized person at the door",
            EventType.PACKAGE: "Package delivered and visible",
            EventType.THREAT: "CRITICAL: Threat detected at the door"
        }
        description = descriptions.get(event_type, "Detection event")

    # If it's a threat and no explanation provided, add one
    if is_threat and not threat_explanation:
        threat_explanation = "Individual is exhibiting suspicious behavior and appears to be concealing their face."

    # Handle case-insensitive ThreatLevel conversion
    try:
        level_str = (threat_confidence or "LOW").upper()
        threat_level = ThreatLevel(level_str)
    except ValueError:
        threat_level = ThreatLevel.UNKNOWN

    # Save uploaded image if provided
    screenshot_url = None
    if image:
        filename = f"demo_{uuid.uuid4()}.jpg"
        file_path = f"app/static/uploads/events/{filename}"
        async with aiofiles.open(file_path, 'wb') as out_file:
            content = await image.read()
            await out_file.write(content)
        screenshot_url = f"/static/uploads/events/{filename}"

    new_event = EventCreate(
        event_type=event_type,
        summary=description,
        is_threat=is_threat,
        threat_confidence=threat_level,
        threat_explanation=threat_explanation,
        conversation=None,
        video_url=None,
        screenshot_url=screenshot_url,
        matched_face_id=matched_face_id
    )
    
    return await repo.create(new_event)


@router.post("/trigger-json", status_code=201)
async def trigger_demo_event_json(
    type: str = Body(..., embed=True),
    description: Optional[str] = Body(None, embed=True),
    is_threat: bool = Body(False, embed=True),
    threat_confidence: Optional[str] = Body("LOW", embed=True),
    threat_explanation: Optional[str] = Body(None, embed=True),
    repo: EventRepository = Depends(get_event_repository)
):
    """
    JSON-only version of the trigger endpoint (no image support).
    Useful for quick testing with curl.
    """
    try:
        event_type = EventType(type)
    except ValueError:
        raise HTTPException(status_code=400, detail=f"Invalid event type: {type}")

    if not description:
        descriptions = {
            EventType.MOTION: "Significant motion detected at front porch",
            EventType.PERSON_DETECTED: "Unrecognized person at the door",
            EventType.PACKAGE: "Package delivered and visible",
            EventType.THREAT: "CRITICAL: Threat detected at the door"
        }
        description = descriptions.get(event_type, "Detection event")

    if is_threat and not threat_explanation:
        threat_explanation = "Individual is exhibiting suspicious behavior and appears to be concealing their face."

    try:
        level_str = (threat_confidence or "LOW").upper()
        threat_level = ThreatLevel(level_str)
    except ValueError:
        threat_level = ThreatLevel.UNKNOWN

    new_event = EventCreate(
        event_type=event_type,
        summary=description,
        is_threat=is_threat,
        threat_confidence=threat_level,
        threat_explanation=threat_explanation,
        conversation=None,
        video_url=None,
        screenshot_url=None,
        matched_face_id=None
    )
    
    return await repo.create(new_event)


@router.get("/status")
async def demo_status():
    return {"status": "Demo mode backend is active"}

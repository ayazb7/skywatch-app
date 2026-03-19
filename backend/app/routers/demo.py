from fastapi import APIRouter, Depends, HTTPException, Body
from typing import Optional
from datetime import datetime
from app.models.event import EventResponse, EventType, ThreatLevel
from app.database.repositories.event_repository import EventRepository
from app.dependencies import get_event_repository
import random

router = APIRouter(prefix="/api/demo", tags=["Demo Mode"])

@router.post("/trigger", status_code=201)
async def trigger_demo_event(
    type: str = Body(..., embed=True),
    description: Optional[str] = Body(None, embed=True),
    is_threat: bool = Body(False, embed=True),
    threat_confidence: Optional[str] = Body("LOW", embed=True),
    threat_explanation: Optional[str] = Body(None, embed=True),
    repo: EventRepository = Depends(get_event_repository)
):
    """
    Manually trigger a mock AI event for demo purposes.
    Inserts an event into the database with the current timestamp.
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

    # Use a fixed ID or let DB handle it (DB handles it via SERIAL)
    # We pass the data to repo.create (which I should check if it exists or use conn.execute)
    
    # Handle case-insensitive ThreatLevel conversion
    try:
        level_str = (threat_confidence or "LOW").upper()
        threat_level = ThreatLevel(level_str)
    except ValueError:
        threat_level = ThreatLevel.UNKNOWN

    # Let's use the repository to save
    from app.models.event import EventCreate
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

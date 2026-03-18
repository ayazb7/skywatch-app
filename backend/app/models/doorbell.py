from pydantic import BaseModel
from typing import Optional

class DoorbellFramePayload(BaseModel):
    image_base64: str
    timestamp: Optional[str] = None

from app.models.event import EventType, ThreatLevel

class DoorbellEventResult(BaseModel):
    event_id: str
    event_type: EventType
    summary: str
    is_familiar: bool
    is_threat: bool = False
    threat_confidence: ThreatLevel = ThreatLevel.UNKNOWN
    threat_explanation: Optional[str] = None
    matched_name: Optional[str] = None

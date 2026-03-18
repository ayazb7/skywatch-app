from pydantic import BaseModel, ConfigDict
from typing import Optional, List
from datetime import datetime
from enum import Enum

class EventType(str, Enum):
    MOTION = "MOTION"
    PERSON_DETECTED = "PERSON_DETECTED"
    THREAT = "THREAT"
    PACKAGE = "PACKAGE"
    OTHER = "OTHER"

class ThreatLevel(str, Enum):
    LOW = "Low"
    MEDIUM = "Medium"
    HIGH = "High"
    UNKNOWN = "Unknown"

class EventCreate(BaseModel):
    event_type: EventType
    summary: str
    conversation: Optional[str] = None
    video_url: Optional[str] = None
    screenshot_url: Optional[str] = None
    is_threat: bool = False
    threat_confidence: ThreatLevel = ThreatLevel.UNKNOWN
    threat_explanation: Optional[str] = None
    matched_face_id: Optional[int] = None

class EventResponse(BaseModel):
    id: str
    description: str
    timestamp: datetime
    type: EventType
    conversation: Optional[str] = None
    video_url: Optional[str] = None
    screenshot_url: Optional[str] = None
    is_threat: bool = False
    threat_confidence: ThreatLevel = ThreatLevel.UNKNOWN
    threat_explanation: Optional[str] = None
    matched_face_id: Optional[int] = None

    model_config = ConfigDict(from_attributes=True)

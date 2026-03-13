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

class EventCreate(BaseModel):
    event_type: EventType
    summary: str
    conversation: Optional[str] = None
    video_url: Optional[str] = None
    screenshot_url: Optional[str] = None

class EventResponse(BaseModel):
    id: str
    description: str
    timestamp: datetime
    type: EventType
    conversation: Optional[str] = None
    video_url: Optional[str] = None
    screenshot_url: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)

from pydantic import BaseModel
from typing import Optional

class DoorbellFramePayload(BaseModel):
    image_base64: str
    timestamp: Optional[str] = None

class DoorbellEventResult(BaseModel):
    event_id: str
    summary: str
    is_familiar: bool
    matched_name: Optional[str] = None

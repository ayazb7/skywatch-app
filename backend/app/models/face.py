from pydantic import BaseModel, ConfigDict
from typing import Optional
from datetime import datetime

class FamiliarFaceCreate(BaseModel):
    name: str
    category: Optional[str] = None
    image_url: Optional[str] = None

class FamiliarFaceResponse(BaseModel):
    id: str
    name: str
    category: Optional[str] = None
    image_url: Optional[str] = None
    created_at: datetime
    
    model_config = ConfigDict(from_attributes=True)

class RecognitionResult(BaseModel):
    is_familiar: bool
    matched_face: Optional[FamiliarFaceResponse] = None
    matched_name: Optional[str] = None
    confidence: Optional[float] = None
    event_summary: Optional[str] = None
    message: Optional[str] = None

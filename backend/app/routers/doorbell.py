from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
import base64
from app.models.doorbell import DoorbellFramePayload, DoorbellEventResult
from app.services.recognition_service import RecognitionService, recognition_service

router = APIRouter(prefix="/api/doorbell", tags=["Doorbell Webhook"])

@router.post("/frame", response_model=DoorbellEventResult)
async def receive_frame_json(payload: DoorbellFramePayload):
    """
    Receive a doorbell frame via JSON payload with base64 encoded image.
    This triggers the entire AI pipeline: Recognition -> Context -> Event Timeline.
    """
    try:
        image_bytes = base64.b64decode(payload.image_base64)
    except Exception as e:
        raise HTTPException(status_code=400, detail="Invalid base64 string")

    result = await recognition_service.process_frame(image_bytes)

    return DoorbellEventResult(
        event_id=result.event_id or "latest",
        event_type=result.event_type,
        summary=result.event_summary or "Unknown",
        is_familiar=result.is_familiar,
        is_threat=result.is_threat,
        threat_confidence=result.threat_confidence,
        threat_explanation=result.threat_explanation,
        matched_name=result.matched_name
    )

@router.post("/upload", response_model=DoorbellEventResult)
async def receive_frame_multipart(file: UploadFile = File(...)):
    """
    Alternative endpoint to receive a doorbell frame via multipart upload, which might be faster for some cameras.
    """
    image_bytes = await file.read()
    result = await recognition_service.process_frame(image_bytes)
    
    return DoorbellEventResult(
        event_id=result.event_id or "latest",
        event_type=result.event_type,
        summary=result.event_summary or "Unknown",
        is_familiar=result.is_familiar,
        is_threat=result.is_threat,
        threat_confidence=result.threat_confidence,
        threat_explanation=result.threat_explanation,
        matched_name=result.matched_name
    )

@router.get("/status")
def doorbell_status():
    return {"status": "listening"}

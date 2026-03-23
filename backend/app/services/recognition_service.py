import logging
import os
import uuid
import aiofiles
from app.models.face import RecognitionResult
from app.models.event import EventCreate, EventType, ThreatLevel
from app.services.deepface_service import deepface_service
from app.database.repositories.face_repository import face_repository
from app.database.repositories.event_repository import event_repository

logger = logging.getLogger(__name__)

class RecognitionService:
    async def process_frame(self, image_bytes: bytes) -> RecognitionResult:
        """
        Orchestrates the recognition pipeline using DeepFace:
        1. Save incoming frame to temporary file.
        2. Fetch all familiar faces from database.
        3. Iterate through each familiar face and call DeepFace.verify.
        4. If a match is found, log PERSON_DETECTED event.
        5. If no match, log MOTION event.
        6. Move temp file to permanent location with clean naming.
        """
        temp_frame_path = f"app/static/uploads/events/temp_{uuid.uuid4()}.jpg"
        
        try:
            # 1. Save frame to temp file
            async with aiofiles.open(temp_frame_path, "wb") as f:
                await f.write(image_bytes)

            # 2. Fetch all familiar faces
            familiar_faces = await face_repository.get_all(limit=100)
            
            is_familiar = False
            matched_face = None
            confidence = 0.0
            event_summary = "Motion detected"
            event_type = EventType.MOTION

            # 3. Compare with each familiar face
            for face in familiar_faces:
                if not face.image_url:
                    continue
                
                stored_image_path = face.image_url.lstrip("/").replace("static/", "app/static/")
                
                if not os.path.exists(stored_image_path):
                    logger.warning(f"Stored image not found for face {face.id}: {stored_image_path}")
                    continue

                result = await deepface_service.verify_faces(temp_frame_path, stored_image_path)
                
                if result and result.get("verified"):
                    is_familiar = True
                    matched_face = face
                    confidence = 1.0 - result.get("distance", 0.5)
                    event_summary = f"{face.name} is at the door"
                    event_type = EventType.PERSON_DETECTED
                    break

            # 3.1. Detect threats using Ollama only if no familiar face is detected
            is_threat = False
            threat_confidence = ThreatLevel.UNKNOWN
            threat_explanation = None

            if not is_familiar:
                from app.services.threat_detection import threat_detection_service
                is_threat, threat_confidence_str, threat_explanation = await threat_detection_service.detect_threat(temp_frame_path)
                try:
                    threat_confidence = ThreatLevel(threat_confidence_str.upper())
                except ValueError:
                    threat_confidence = ThreatLevel.UNKNOWN
                
                if is_threat:
                    event_type = EventType.THREAT
                    event_summary = "Threat detected at the door"
                    logger.warning(f"Threat detected at the door! Confidence: {threat_confidence}")

            # 4. Handle file persistence BEFORE creating database record
            # Use a clean persistent filename (stripping temp_ prefix)
            perm_filename = os.path.basename(temp_frame_path).replace("temp_", "capture_")
            perm_event_path = f"app/static/uploads/events/{perm_filename}"
            
            # Save the file permanently
            os.rename(temp_frame_path, perm_event_path)
            temp_frame_path = perm_event_path # Point cleanup to new path
            
            # 5. Log the event to the timeline using the persistent path
            event_create = EventCreate(
                event_type=event_type,
                summary=event_summary,
                is_threat=is_threat,
                threat_confidence=threat_confidence,
                threat_explanation=threat_explanation,
                matched_face_id=matched_face.id if matched_face else None,
                screenshot_url=f"/static/uploads/events/{perm_filename}"
            )
            
            created_event = await event_repository.create(event_create)
            logger.info(f"Created event history record {created_event.id}: {event_summary}")

            return RecognitionResult(
                is_familiar=is_familiar,
                event_id=str(created_event.id),
                event_type=event_type,
                is_threat=is_threat,
                threat_confidence=threat_confidence,
                threat_explanation=threat_explanation,
                matched_face=matched_face,
                matched_name=matched_face.name if matched_face else None,
                confidence=confidence,
                event_summary=event_summary,
                message=f"Familiar face detected: {matched_face.name}" if is_familiar else "No familiar faces detected"
            )

        except Exception as e:
            logger.error(f"Error in recognition pipeline: {e}")
            return RecognitionResult(
                is_familiar=False,
                event_summary="Error processing doorbell frame"
            )
        finally:
            # ONLY delete if it's still a temporary file (meaning it was NEVER moved/saved)
            if "temp_" in os.path.basename(temp_frame_path) and os.path.exists(temp_frame_path):
                try:
                    os.remove(temp_frame_path)
                except:
                    pass

recognition_service = RecognitionService()

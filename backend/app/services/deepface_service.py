from deepface import DeepFace
import logging
from typing import Dict, Any, Optional

logger = logging.getLogger(__name__)

class DeepFaceService:
    def __init__(self):
        self.model_name = "VGG-Face"
        self.detector_backend = "opencv"

    def warmup(self):
        """
        Pre-load the DeepFace models into memory to avoid delay on first request.
        """
        logger.info(f"Loading DeepFace model: {self.model_name}...")
        try:
            DeepFace.build_model(self.model_name)
            logger.info("DeepFace model loaded successfully.")
        except Exception as e:
            logger.error(f"Failed to load DeepFace model: {e}")

    async def verify_faces(self, img1_path: str, img2_path: str) -> Optional[Dict[str, Any]]:
        """
        Verify if two faces are the same person.
        """
        try:
            result = DeepFace.verify(
                img1_path = img1_path,
                img2_path = img2_path,
                model_name = self.model_name,
                detector_backend = self.detector_backend,
                enforce_detection = False 
            )
            return result
        except Exception as e:
            logger.error(f"DeepFace verification error: {e}")
            return None

deepface_service = DeepFaceService()

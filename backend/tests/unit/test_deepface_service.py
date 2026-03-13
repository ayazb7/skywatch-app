import pytest
from unittest.mock import patch, MagicMock
from app.services.deepface_service import DeepFaceService

@pytest.fixture
def deepface_service():
    return DeepFaceService()

@patch('app.services.deepface_service.DeepFace.verify')
@pytest.mark.asyncio
async def test_verify_faces_success(mock_verify, deepface_service):
    # Mock result from DeepFace.verify
    mock_verify.return_value = {
        "verified": True,
        "distance": 0.1,
        "threshold": 0.4,
        "model": "VGG-Face",
        "detector_backend": "opencv",
        "similarity_metric": "cosine"
    }
    
    result = await deepface_service.verify_faces("path/to/img1.jpg", "path/to/img2.jpg")
    
    assert result is not None
    assert result["verified"] is True
    assert result["distance"] == 0.1
    mock_verify.assert_called_once()

@patch('app.services.deepface_service.DeepFace.verify')
@pytest.mark.asyncio
async def test_verify_faces_fail(mock_verify, deepface_service):
    mock_verify.side_effect = Exception("Model not loaded")
    
    result = await deepface_service.verify_faces("path/to/img1.jpg", "path/to/img2.jpg")
    
    assert result is None

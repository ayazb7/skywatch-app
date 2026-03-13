import pytest
from unittest.mock import patch, MagicMock, AsyncMock
from app.services.recognition_service import RecognitionService
from app.models.face import FamiliarFaceResponse
from app.models.event import EventResponse, EventType
from datetime import datetime

@pytest.fixture
def recognition_service():
    return RecognitionService()

@patch('app.services.recognition_service.face_repository.get_all', new_callable=AsyncMock)
@patch('app.services.recognition_service.deepface_service.verify_faces', new_callable=AsyncMock)
@patch('app.services.recognition_service.event_repository.create', new_callable=AsyncMock)
@patch('app.services.recognition_service.aiofiles.open')
@patch('app.services.recognition_service.os.path.exists')
@patch('app.services.recognition_service.os.remove')
@pytest.mark.asyncio
async def test_process_frame_familiar(
    mock_remove, mock_exists, mock_aio_open, mock_event_create, 
    mock_verify, mock_get_faces, recognition_service
):
    # Setup mocks
    mock_exists.return_value = True # Stored image exists
    
    # 1. Mock familiar faces in DB
    mock_get_faces.return_value = [
        FamiliarFaceResponse(
            id="1", name="John Doe", category="Family", 
            image_url="/static/uploads/faces/john.jpg", 
            created_at=datetime.now()
        )
    ]
    
    # 2. Mock DeepFace verification success
    mock_verify.return_value = {"verified": True, "distance": 0.1}
    
    # 3. Mock Event creation
    mock_event_create.return_value = EventResponse(
        id="101", description="John Doe is at the door", 
        timestamp=datetime.now(), type=EventType.PERSON_DETECTED
    )

    # 4. Mock aiofiles
    mock_f = MagicMock()
    mock_f.write = AsyncMock()
    mock_aio_open.return_value.__aenter__.return_value = mock_f

    # Run
    result = await recognition_service.process_frame(b"fake_image_bytes")

    # Assert
    assert result.is_familiar is True
    assert result.matched_face.name == "John Doe"
    assert "John Doe is at the door" in result.event_summary
    mock_verify.assert_called_once()
    mock_event_create.assert_called_once()

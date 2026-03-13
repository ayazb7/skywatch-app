from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, Form
from typing import List, Optional
import os
import uuid
import aiofiles
from app.models.face import FamiliarFaceResponse, FamiliarFaceCreate
from app.database.repositories.face_repository import FaceRepository
from app.database.repositories.face_repository import FaceRepository
from app.dependencies import get_face_repository

router = APIRouter(prefix="/api/faces", tags=["Familiar Faces"])

@router.get("", response_model=List[FamiliarFaceResponse])
async def list_faces(limit: int = 50, offset: int = 0, repo: FaceRepository = Depends(get_face_repository)):
    return await repo.get_all(limit, offset)

@router.post("", response_model=FamiliarFaceResponse, status_code=201)
async def create_face(
    name: str = Form(...),
    category: Optional[str] = Form(None),
    image: UploadFile = File(...),
    repo: FaceRepository = Depends(get_face_repository)
):
    # 1. Read bytes representing the face
    image_bytes = await image.read()

    # 2. Save image to local disk
    filename = f"{uuid.uuid4()}.jpg"
    file_path = f"app/static/uploads/faces/{filename}"
    async with aiofiles.open(file_path, 'wb') as out_file:
        await out_file.write(image_bytes)
        
    image_url = f"/static/uploads/faces/{filename}"

    # 3. Add to local database metadata
    try:
        new_face = FamiliarFaceCreate(name=name, category=category, image_url=image_url)
        created_face = await repo.create(new_face)
        return created_face
    except Exception as e:
        if os.path.exists(file_path):
            os.remove(file_path)
        raise HTTPException(status_code=500, detail=f"Failed to save face metadata: {str(e)}")

@router.delete("/{face_id}", status_code=204)
async def delete_face(face_id: str, repo: FaceRepository = Depends(get_face_repository)):
    face = await repo.get_by_id(face_id)
    if not face:
        raise HTTPException(status_code=404, detail="Face not found")

    # 1. Remove from local DB
    success = await repo.delete(face_id)
    if not success:
        raise HTTPException(status_code=500, detail="Failed to remove face from database metadata")
        
    # 2. Delete file from local disk
    if face.image_url:
        file_path = face.image_url.lstrip("/")
        if file_path.startswith("static"):
            file_path = "app/" + file_path
        if os.path.exists(file_path):
            os.remove(file_path)

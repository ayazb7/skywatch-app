from typing import List, Optional
from app.database.connection import get_pool
from app.models.face import FamiliarFaceCreate, FamiliarFaceResponse

class FaceRepository:
    async def get_all(self, limit: int = 50, offset: int = 0) -> List[FamiliarFaceResponse]:
        pool = await get_pool()
        async with pool.acquire() as conn:
            records = await conn.fetch(
                """
                SELECT id, name, category, image_url, created_at
                FROM familiar_faces
                ORDER BY created_at DESC
                LIMIT $1 OFFSET $2
                """,
                limit, offset
            )
            return [
                FamiliarFaceResponse(
                    id=str(r["id"]),
                    name=r["name"],
                    category=r["category"],
                    image_url=r["image_url"],
                    created_at=r["created_at"]
                )
                for r in records
            ]

    async def get_by_id(self, face_id: str) -> Optional[FamiliarFaceResponse]:
        pool = await get_pool()
        async with pool.acquire() as conn:
            r = await conn.fetchrow(
                """
                SELECT id, name, category, image_url, created_at
                FROM familiar_faces
                WHERE id = $1
                """,
                int(face_id)
            )
            if r:
                return FamiliarFaceResponse(
                    id=str(r["id"]),
                    name=r["name"],
                    category=r["category"],
                    image_url=r["image_url"],
                    created_at=r["created_at"]
                )
            return None

    async def create(self, face: FamiliarFaceCreate) -> FamiliarFaceResponse:
        pool = await get_pool()
        async with pool.acquire() as conn:
            r = await conn.fetchrow(
                """
                INSERT INTO familiar_faces (name, category, image_url)
                VALUES ($1, $2, $3)
                RETURNING id, name, category, image_url, created_at
                """,
                face.name, face.category, face.image_url
            )
            return FamiliarFaceResponse(
                id=str(r["id"]),
                name=r["name"],
                category=r["category"],
                image_url=r["image_url"],
                created_at=r["created_at"]
            )

    async def delete(self, face_id: str) -> bool:
        pool = await get_pool()
        async with pool.acquire() as conn:
            result = await conn.execute(
                "DELETE FROM familiar_faces WHERE id = $1",
                int(face_id)
            )
            return result == "DELETE 1"

face_repository = FaceRepository()

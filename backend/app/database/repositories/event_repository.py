from typing import List, Optional
from datetime import datetime
from app.database.connection import get_pool
from app.models.event import EventCreate, EventResponse, EventType, ThreatLevel

class EventRepository:
    async def get_all(self, limit: int = 50, offset: int = 0) -> List[EventResponse]:
        pool = await get_pool()
        async with pool.acquire() as conn:
            records = await conn.fetch(
                """
                SELECT e.id, e.type, e.description, e.conversation, e.video_url, e.screenshot_url, e.timestamp, 
                       e.is_threat, e.threat_confidence, e.threat_explanation, e.matched_face_id,
                       f.name as matched_face_name, f.image_url as matched_face_image_url
                FROM event_history e
                LEFT JOIN familiar_faces f ON e.matched_face_id = f.id
                ORDER BY e.timestamp DESC
                LIMIT $1 OFFSET $2
                """,
                limit, offset
            )
            return [
                EventResponse(
                    id=str(r["id"]),
                    type=EventType(r["type"]),
                    description=r["description"],
                    timestamp=r["timestamp"],
                    conversation=r["conversation"],
                    video_url=r["video_url"],
                    screenshot_url=r["screenshot_url"],
                    is_threat=r["is_threat"],
                    threat_confidence=ThreatLevel(r["threat_confidence"]),
                    threat_explanation=r["threat_explanation"],
                    matched_face_id=r["matched_face_id"],
                    matched_face_name=r["matched_face_name"],
                    matched_face_image_url=r["matched_face_image_url"]
                )
                for r in records
            ]

    async def get_by_id(self, event_id: str) -> Optional[EventResponse]:
        pool = await get_pool()
        async with pool.acquire() as conn:
            r = await conn.fetchrow(
                """
                SELECT e.id, e.type, e.description, e.conversation, e.video_url, e.screenshot_url, e.timestamp, 
                       e.is_threat, e.threat_confidence, e.threat_explanation, e.matched_face_id,
                       f.name as matched_face_name, f.image_url as matched_face_image_url
                FROM event_history e
                LEFT JOIN familiar_faces f ON e.matched_face_id = f.id
                WHERE e.id = $1
                """,
                int(event_id)
            )
            if r:
                return EventResponse(
                    id=str(r["id"]),
                    type=EventType(r["type"]),
                    description=r["description"],
                    timestamp=r["timestamp"],
                    conversation=r["conversation"],
                    video_url=r["video_url"],
                    screenshot_url=r["screenshot_url"],
                    is_threat=r["is_threat"],
                    threat_confidence=ThreatLevel(r["threat_confidence"]),
                    threat_explanation=r["threat_explanation"],
                    matched_face_id=r["matched_face_id"],
                    matched_face_name=r["matched_face_name"],
                    matched_face_image_url=r["matched_face_image_url"]
                )
            return None

    async def create(self, event: EventCreate) -> EventResponse:
        pool = await get_pool()
        async with pool.acquire() as conn:
            # We insert and then return the full row with join (or just the row)
            # Since create usually just needs the created ID, we can do another fetch or just return row
            r = await conn.fetchrow(
                """
                WITH inserted AS (
                    INSERT INTO event_history (type, description, conversation, video_url, screenshot_url, is_threat, 
                                               threat_confidence, threat_explanation, matched_face_id)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
                    RETURNING id, type, description, conversation, video_url, screenshot_url, timestamp, is_threat, 
                              threat_confidence, threat_explanation, matched_face_id
                )
                SELECT i.*, f.name as matched_face_name, f.image_url as matched_face_image_url
                FROM inserted i
                LEFT JOIN familiar_faces f ON i.matched_face_id = f.id
                """,
                event.event_type.value, event.summary, event.conversation, event.video_url, event.screenshot_url,
                event.is_threat, event.threat_confidence.value, event.threat_explanation, event.matched_face_id
            )
            return EventResponse(
                id=str(r["id"]),
                type=EventType(r["type"]),
                description=r["description"],
                timestamp=r["timestamp"],
                conversation=r["conversation"],
                video_url=r["video_url"],
                screenshot_url=r["screenshot_url"],
                is_threat=r["is_threat"],
                threat_confidence=ThreatLevel(r["threat_confidence"]),
                threat_explanation=r["threat_explanation"],
                matched_face_id=r["matched_face_id"],
                matched_face_name=r["matched_face_name"],
                matched_face_image_url=r["matched_face_image_url"]
            )

    async def delete(self, event_id: str) -> bool:
        pool = await get_pool()
        async with pool.acquire() as conn:
            result = await conn.execute(
                "DELETE FROM event_history WHERE id = $1",
                int(event_id)
            )
            return result == "DELETE 1"

    async def get_by_date(self, date_str: str, limit: int = 50, offset: int = 0) -> List[EventResponse]:
        """Fetch events whose timestamp falls on the given date (YYYY-MM-DD)."""
        from datetime import date
        target_date = date.fromisoformat(date_str)
        pool = await get_pool()
        async with pool.acquire() as conn:
            records = await conn.fetch(
                """
                SELECT e.id, e.type, e.description, e.conversation, e.video_url, e.screenshot_url, e.timestamp, 
                       e.is_threat, e.threat_confidence, e.threat_explanation, e.matched_face_id,
                       f.name as matched_face_name, f.image_url as matched_face_image_url
                FROM event_history e
                LEFT JOIN familiar_faces f ON e.matched_face_id = f.id
                WHERE e.timestamp::date = $1
                ORDER BY e.timestamp DESC
                LIMIT $2 OFFSET $3
                """,
                target_date, limit, offset
            )
            return [
                EventResponse(
                    id=str(r["id"]),
                    type=EventType(r["type"]),
                    description=r["description"],
                    timestamp=r["timestamp"],
                    conversation=r["conversation"],
                    video_url=r["video_url"],
                    screenshot_url=r["screenshot_url"],
                    is_threat=r["is_threat"],
                    threat_confidence=ThreatLevel(r["threat_confidence"]),
                    threat_explanation=r["threat_explanation"],
                    matched_face_id=r["matched_face_id"],
                    matched_face_name=r["matched_face_name"],
                    matched_face_image_url=r["matched_face_image_url"]
                )
                for r in records
            ]

event_repository = EventRepository()

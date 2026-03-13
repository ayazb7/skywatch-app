from typing import List, Optional
from datetime import datetime
from app.database.connection import get_pool
from app.models.event import EventCreate, EventResponse

class EventRepository:
    async def get_all(self, limit: int = 50, offset: int = 0) -> List[EventResponse]:
        pool = await get_pool()
        async with pool.acquire() as conn:
            records = await conn.fetch(
                """
                SELECT id, type, description, conversation, video_url, screenshot_url, timestamp
                FROM event_history
                ORDER BY timestamp DESC
                LIMIT $1 OFFSET $2
                """,
                limit, offset
            )
            return [
                EventResponse(
                    id=str(r["id"]),
                    type=r["type"],
                    description=r["description"],
                    timestamp=r["timestamp"],
                    conversation=r["conversation"],
                    video_url=r["video_url"],
                    screenshot_url=r["screenshot_url"]
                )
                for r in records
            ]

    async def get_by_id(self, event_id: str) -> Optional[EventResponse]:
        pool = await get_pool()
        async with pool.acquire() as conn:
            r = await conn.fetchrow(
                """
                SELECT id, type, description, conversation, video_url, screenshot_url, timestamp
                FROM event_history
                WHERE id = $1
                """,
                int(event_id)
            )
            if r:
                return EventResponse(
                    id=str(r["id"]),
                    type=r["type"],
                    description=r["description"],
                    timestamp=r["timestamp"],
                    conversation=r["conversation"],
                    video_url=r["video_url"],
                    screenshot_url=r["screenshot_url"]
                )
            return None

    async def create(self, event: EventCreate) -> EventResponse:
        pool = await get_pool()
        async with pool.acquire() as conn:
            r = await conn.fetchrow(
                """
                INSERT INTO event_history (type, description, conversation, video_url, screenshot_url)
                VALUES ($1, $2, $3, $4, $5)
                RETURNING id, type, description, conversation, video_url, screenshot_url, timestamp
                """,
                event.event_type, event.summary, event.conversation, event.video_url, event.screenshot_url
            )
            return EventResponse(
                id=str(r["id"]),
                type=r["type"],
                description=r["description"],
                timestamp=r["timestamp"],
                conversation=r["conversation"],
                video_url=r["video_url"],
                screenshot_url=r["screenshot_url"]
            )

    async def delete(self, event_id: str) -> bool:
        pool = await get_pool()
        async with pool.acquire() as conn:
            result = await conn.execute(
                "DELETE FROM event_history WHERE id = $1",
                int(event_id)
            )
            return result == "DELETE 1"

event_repository = EventRepository()

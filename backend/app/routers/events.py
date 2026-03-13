from fastapi import APIRouter, Depends, HTTPException
from typing import List
from app.models.event import EventResponse, EventCreate
from app.database.repositories.event_repository import EventRepository
from app.dependencies import get_event_repository

router = APIRouter(prefix="/api/events", tags=["Events Timeline"])

@router.get("", response_model=List[EventResponse])
async def list_events(limit: int = 50, offset: int = 0, repo: EventRepository = Depends(get_event_repository)):
    return await repo.get_all(limit, offset)

@router.get("/{event_id}", response_model=EventResponse)
async def get_event(event_id: str, repo: EventRepository = Depends(get_event_repository)):
    event = await repo.get_by_id(event_id)
    if not event:
        raise HTTPException(status_code=404, detail="Event not found")
    return event

@router.delete("/{event_id}", status_code=204)
async def delete_event(event_id: str, repo: EventRepository = Depends(get_event_repository)):
    success = await repo.delete(event_id)
    if not success:
        raise HTTPException(status_code=404, detail="Event not found")

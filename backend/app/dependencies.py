from app.database.repositories.event_repository import EventRepository, event_repository
from app.database.repositories.face_repository import FaceRepository, face_repository


def get_event_repository() -> EventRepository:
    return event_repository

def get_face_repository() -> FaceRepository:
    return face_repository

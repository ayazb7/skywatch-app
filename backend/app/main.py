from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import traceback

from app.config import settings
from app.database import connection
from app.routers import events, faces, doorbell, demo
from app.services.deepface_service import deepface_service
from fastapi.staticfiles import StaticFiles
import os

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Ensure static directories exist
    os.makedirs("app/static/uploads/faces", exist_ok=True)
    os.makedirs("app/static/uploads/events", exist_ok=True)
    
    # Pre-load DeepFace models
    deepface_service.warmup()
    
    # Pre-load/Verify Threat Detection models
    from app.services.threat_detection import threat_detection_service
    threat_detection_service.warmup()
    
    # Startup
    try:
        await connection.init_db()
        print("Database connection pool initialized.")
    except Exception as e:
        print(f"Failed to initialize database: {e}")
        traceback.print_exc()
    
    yield
    
    # Shutdown
    await connection.close_db()
    print("Database connection pool closed.")

app = FastAPI(
    title="SkyWatch API",
    description="Backend API for SkyWatch doorbell service",
    lifespan=lifespan
)

# Set up CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/api/health")
async def health_check():
    return {"status": "ok"}

# Include routers
app.include_router(events.router)
app.include_router(faces.router)
app.include_router(doorbell.router)
app.include_router(demo.router)

# Mount static files
app.mount("/static", StaticFiles(directory="app/static"), name="static")

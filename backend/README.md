# SkyWatch Backend

This is the FastAPI backend for the SkyWatch AI doorbell system.

## Requirements

- Python 3.10 or higher
- PostgreSQL
- Ollama (for threat detection)

## Setup

1. Create a virtual environment:
   ```bash
   python -m venv .venv
   source .venv/bin/activate
   ```

2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

## Database Configuration

1. Create the database and role in PostgreSQL:
   ```sql
   CREATE DATABASE skywatch_app;
   CREATE ROLE skywatch_user WITH LOGIN PASSWORD 'skywatch_pass';
   GRANT ALL PRIVILEGES ON DATABASE skywatch_app TO skywatch_user;
   ```

2. Initialize the schema:
   Running the application for the first time will attempt to initialize the database from `database/init.sql`. Alternatively, you can run the SQL manually in `psql`.

## AI Models

### Face Recognition (DeepFace)
Models are automatically downloaded and cached in `~/.deepface` on the first run.

### Threat Detection (Ollama)
The system uses the `qwen3.5` vision model.
- The backend will attempt to pull the model automatically on startup if it is missing.
- To pull it manually:
  ```bash
  ollama pull qwen3.5
  ```

## Running the Application

Start the server using uvicorn:
```bash
PYTHONPATH=. uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

The API documentation will be available at `http://localhost:8000/docs`.

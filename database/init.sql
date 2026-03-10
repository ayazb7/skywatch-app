-- Enable the pgvector extension to work with embeddings
CREATE EXTENSION IF NOT EXISTS vector;

-- Table to store familiar faces and embeddings
CREATE TABLE IF NOT EXISTS familiar_faces (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    category TEXT, 
    embedding VECTOR(1536),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table to store event history
CREATE TABLE IF NOT EXISTS event_history (
    id SERIAL PRIMARY KEY,
    conversation TEXT,
    video_url TEXT,
    screenshot_url TEXT,
    summary TEXT,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ON familiar_faces USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- Types for Enums
DO $$ BEGIN
    CREATE TYPE event_type AS ENUM ('MOTION', 'PERSON_DETECTED', 'THREAT', 'PACKAGE', 'OTHER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE threat_level AS ENUM ('Low', 'Medium', 'High', 'Unknown');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Table to store familiar faces metadata
CREATE TABLE IF NOT EXISTS familiar_faces (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    category TEXT,
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table to store event history
CREATE TABLE IF NOT EXISTS event_history (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    type TEXT NOT NULL,
    conversation TEXT,
    video_url TEXT,
    screenshot_url TEXT,
    is_threat BOOLEAN DEFAULT FALSE,
    threat_confidence TEXT DEFAULT 'Unknown',
    threat_explanation TEXT,
    matched_face_id INTEGER REFERENCES familiar_faces(id) ON DELETE SET NULL
);

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
    screenshot_url TEXT
);

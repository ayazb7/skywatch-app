# SkyWatch Event History JSON Server

Mock JSON server for the SkyWatch app's event history API.

## Setup

```bash
npm install -g json-server
```

## Run Server

```bash
# Local access
json-server --watch eventHistory.json --port 3000

# Mobile device access
json-server --watch eventHistory.json --host 0.0.0.0 --port 3000
```

Server runs at `http://localhost:3000`

## Endpoints

```
GET    /events       # Get all events
GET    /events/:id   # Get single event
POST   /events       # Create event
PUT    /events/:id   # Update event
DELETE /events/:id   # Delete event
```

### Query Examples
```
GET /events?_sort=timestamp&_order=desc  # Sort by timestamp
GET /events?_page=1&_limit=10            # Pagination
GET /events?q=delivery                   # Search
```

## Mobile Access

- **Android Emulator**: `http://10.0.2.2:3000`
- **iOS Simulator**: `http://localhost:3000`
- **Physical Device**: `http://YOUR_IP:3000` (e.g., `http://192.168.1.100:3000`)


# Skywatch Database Setup

This directory contains the database initialization scripts for the Skywatch app, using PostgreSQL with the `pgvector` extension.

## Prerequisites

- **For Docker setup:** [Docker](https://www.docker.com/) installed and running on your machine.
- **For Local Native setup (macOS):** [Homebrew](https://brew.sh/) installed.

## Running the Database via Docker

1.  **Pull the `pgvector` Docker image:**
    Use the `pgvector/pgvector` image which comes with PostgreSQL and the vector extension pre-installed.

    ```bash
    docker pull pgvector/pgvector:pg16
    ```

2.  **Run the PostgreSQL container:**
    Run the following command to start a PostgreSQL instance. This command maps the `init.sql` script to the container's initialization directory so it runs automatically on first startup.

    ```bash
    docker run --name skywatch-db \
      -e POSTGRES_USER=skywatch_user \
      -e POSTGRES_PASSWORD=skywatch_pass \
      -e POSTGRES_DB=skywatch_db \
      -p 5432:5432 \
      -v "$(pwd)/init.sql":/docker-entrypoint-initdb.d/init.sql \
      -d pgvector/pgvector:pg16
    ```

    *   **Username:** `skywatch_user`
    *   **Password:** `skywatch_pass`
    *   **Database:** `skywatch_db`
    *   **Port:** `5432`

3.  **Verify the setup:**
    You can check if the container is running:
    ```bash
    docker ps
    ```

    You can also connect to the database to verify tables were created:
    ```bash
    docker exec -it skywatch-db psql -U skywatch_user -d skywatch_db
    ```
    Inside the psql shell, run `\dt` to list tables. You should see `familiar_faces` and `event_history`.

4.  **Stop/Start the container:**
    ```bash
    docker stop skywatch-db
    docker start skywatch-db
    ```

5.  **Remove the container (to start fresh):**
    ```bash
    docker stop skywatch-db
    docker rm skywatch-db
    ```

## Running the Database Locally (macOS with Postgres.app)

If you prefer to run PostgreSQL directly on your machine without Docker, follow these steps using [Postgres.app](https://postgresapp.com/).

### Prerequisites

-   **Postgres.app:** [Download and install here](https://postgresapp.com/).
-   **Command Line Tools:** In Postgres.app, go to **Settings > Install CLI Tools** to enable `psql` in your Terminal.

### Setup

1.  **Create the Database:**
    Open your Terminal and create the database instance:
    ```bash
    createdb skywatch_db
    ```

2.  **Initialize Schema & pgvector:**
    Run the `init.sql` script to enable the vector extension, create tables, and set up indices. Run this command from the `database` directory:
    ```bash
    psql -d skywatch_db -f init.sql
    ```

3.  **Connection Details:**
    Postgres.app uses your macOS username by default and typically doesn't require a password for local connections.

    | Parameter | Value |
    |-----------|-------|
    | Host      | `localhost` |
    | Port      | `5432` |
    | Database  | `skywatch_db` |
    | User      | *(Your macOS username)* |
    | Password  | *(Leave blank/empty)* |

    > **Tip:** If you aren't sure of your username, run `whoami` in the Terminal.

### Maintenance Commands

-   **Verify the Setup:**
    Connect to the database to verify your tables and extensions:
    ```bash
    psql skywatch_db
    ```
    Inside the `psql` prompt, run:
    -   `\dx` — List extensions (verify `vector` is present).
    -   `\dt` — List tables (verify `familiar_faces` and `event_history`).
    -   `\q` — Quit the prompt.

-   **Stop/Start the Server:**
    -   To **Stop**: Click the Stop button in the Postgres.app window.
    -   To **Start**: Click the Start button in the Postgres.app window.

### Troubleshooting: "Port 5432 is in use"

If the server fails to start because the port is blocked, identify the process and kill it:
```bash
# Find the Process ID (PID)
sudo lsof -i :5432

# Kill the process (replace <PID> with the number found above)
sudo kill -9 <PID>
```

### GUI Client: Postico

If you prefer a visual interface for browsing and managing your database, you can use [Postico](https://eggerapps.at/postico2/). Simply create a new connection using the connection details above (host: `localhost`, port: `5432`, database: `skywatch_db`, your macOS username, no password).

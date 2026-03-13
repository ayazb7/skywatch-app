# Skywatch Database Setup

This directory contains the database initialization scripts for the Skywatch app. We use **PostgreSQL** for metadata and local storage for AI images.

## Local setup (macOS with Postgres.app)

The easiest way to run the database is using [Postgres.app](https://postgresapp.com/).

### Setup steps:

1.  **Create the Database:**
    Open your Terminal and run:
    ```bash
    createdb skywatch_app
    ```

2.  **Initialize Schema:**
    Run the `init.sql` script to create the necessary tables. Run this command from the project root or the `database/` directory:
    ```bash
    psql -d skywatch_app -f init.sql
    ```

3.  **Connection Details:**
    Postgres.app usually uses your macOS username by default.

    | Parameter | Value |
    |-----------|-------|
    | Host      | `localhost` |
    | Port      | `5432` |
    | Database  | `skywatch_app` |
    | User      | *(Your macOS username)* |
    | Password  | *(Leave empty or your Postgres password)* |

## Maintenance

-   **Verify the Setup:**
    ```bash
    psql skywatch_app
    ```
    -   `\dt` — List tables (verify `familiar_faces` and `event_history`).
    -   `\q` — Quit.

-   **Clean Start:**
    If you need to start fresh, you can drop and recreate the database:
    ```bash
    dropdb skywatch_app
    createdb skywatch_app
    psql -d skywatch_app -f init.sql
    ```

## GUI Recommendation: Postico

Use [Postico](https://eggerapps.at/postico2/) to browse your data easily. Connect to `localhost` / `skywatch_app`.

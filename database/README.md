# Skywatch Database Setup

This directory contains the database initialization scripts for the Skywatch app, using PostgreSQL with the `pgvector` extension.

## Prerequisites

- [Docker](https://www.docker.com/) installed and running on your machine.

## Running the Database Locally

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

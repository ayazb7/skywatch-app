import asyncpg
from typing import Optional
from app.config import settings

class Database:
    def __init__(self):
        self.pool: Optional[asyncpg.Pool] = None

    async def connect(self):
        if self.pool is None:
            self.pool = await asyncpg.create_pool(
                dsn=settings.DATABASE_URL,
                min_size=1,
                max_size=10
            )

    async def close(self):
        if self.pool is not None:
            await self.pool.close()
            self.pool = None

db = Database()

async def init_db():
    await db.connect()

async def close_db():
    await db.close()

async def get_pool() -> asyncpg.Pool:
    if db.pool is None:
        await db.connect()
    return db.pool

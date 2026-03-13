from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import List

class Settings(BaseSettings):
    DATABASE_URL: str = "postgresql://skywatch_user:skywatch_pass@localhost:5432/skywatch_app"
    CORS_ORIGINS: List[str] = ["http://localhost:3000", "http://10.0.2.2:3000", "*"]

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

settings = Settings()

import os
from config import PROJECT_ROOT


class AppConfig:
    DEBUG = bool(int(os.getenv("DEBUG", 0)))

    SESSION_COOKIE_HTTPONLY = True
    SESSION_COOKIE_SAMESITE = "Lax"

    DATABASE_URI = f"sqlite:///:memory:"

    LOGS_DIR_PATH = os.path.join(PROJECT_ROOT, "logs")
    STORAGE_DIR_PATH = os.path.join(PROJECT_ROOT, "storage")

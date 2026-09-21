__version__ = "0.0.0"


from flask import Flask
import secrets
from config import AppConfig
from slingshot_be.io_logging import AppLogger
from slingshot_be.db import Database

db = Database()


def generate_secret(is_debug=False):
    return secrets.token_hex(
        nbytes=32) if not is_debug else "debug_secret"

def register_blueprints(app: Flask):
    from slingshot_be import blueprints

    app.register_blueprint(blueprints.api_blueprint)

def setup_app_modules(app: Flask):
    app_logger = AppLogger(
        app=app,
        logs_filename="app.log",
        logs_path=app.config.get("LOGS_DIR_PATH"),
        backup_log_files_count=3)

    app_logger.setup()

    db.setup(app.config["DATABASE_URI"])
    db.create_all()

    app.logger.info("app modules setup completed...")

def create_app(config_class: type[AppConfig]):
    app = Flask(__name__, instance_relative_config=False, static_folder=None)

    app.config.from_object(config_class)
    app.secret_key = generate_secret(is_debug=app.debug)

    setup_app_modules(app)

    with app.app_context():
        register_blueprints(app)

        app.logger.info("app has been created")

        return app

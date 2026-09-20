__version__ = "0.0.0"


from flask import Flask

def create_app():
    app = Flask(__name__, instance_relative_config=False, static_folder=None)

    with app.app_context():
        return app

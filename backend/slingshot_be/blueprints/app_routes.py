from flask import current_app
from slingshot_be import db


@current_app.teardown_appcontext
def teardown_appcontext(exception=None):
    db.close_session(exception=exception)

from flask import Blueprint, make_response, jsonify
from slingshot_be import models


api_blueprint = Blueprint("api", __name__, url_prefix="/api/v1")


@api_blueprint.route("/health-check", methods=["GET"])
def home():
    return make_response(jsonify({"status": "ok"}), 200)

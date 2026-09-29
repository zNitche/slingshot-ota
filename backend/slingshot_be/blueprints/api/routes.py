from flask import Blueprint, make_response, jsonify, \
    current_app, url_for, send_file, abort
from werkzeug.utils import secure_filename
import os


api_blueprint = Blueprint("api", __name__, url_prefix="/api/v1")


@api_blueprint.route("/health-check", methods=["GET"])
def health_check():
    return make_response(jsonify({"status": "ok"}), 200)


@api_blueprint.route("/<string:app_version>/current-revision/<string:item>", methods=["GET"])
def current_revision_item(app_version: str, item: str):
    app_version = secure_filename(app_version)
    allowed_items = ["release.zip.sig", "release.zip"]

    if item not in allowed_items:
        abort(404)

    storage_path = os.path.join(
        current_app.config["STORAGE_DIR_PATH"], app_version)

    return send_file(os.path.join(storage_path, item))


@api_blueprint.route("/<string:app_version>/current-revision", methods=["GET"])
def current_revision(app_version: str):
    app_version = secure_filename(app_version)
    storage_path = os.path.join(
        current_app.config["STORAGE_DIR_PATH"], app_version)

    does_release_file_exist = os.path.exists(
        os.path.join(storage_path, "release.zip"))

    if not does_release_file_exist:
        return make_response(jsonify({"exists": False}), 200)

    with open(os.path.join(storage_path, "release.zip.sha256"),
              "r") as hash_file:
        sha256sum = hash_file.read().split("=")[1].replace(" ", "")

    release_url = url_for("api.current_revision_item",
                          app_version=app_version,
                          item="release.zip", _external=True)
    sig_url = url_for("api.current_revision_item",
                      app_version=app_version,
                      item="release.zip.sig", _external=True)

    return make_response(jsonify({"exists": True,
                                  "sha256sum": sha256sum,
                                  "release_url": release_url,
                                  "sig_url": sig_url},
                                 ), 200)

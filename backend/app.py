from slingshot_be import create_app


app = create_app()


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=8080,
            threaded=True, load_dotenv=False)

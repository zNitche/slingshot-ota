# slingshot-ota
Proof of Concept for Capacitor.js Over-the-Air updater plugin.

### Description
So so so, as stated above this repo contains Capacitor.js plugin (example PWA app, backend and plugin's codebase) which enables OTA updates for PWA apps.

Just to add, I've tested this on a couple of devices and it works just fine, but I am still considering this project (at it's current state) as a Proof of Concept.

### How it works
1. At startup app sends request to backend to check if an update is available for current app's version.
2. If so, update files are downloaded and saved to temporary directory.
3. Update's zip archieve origin is validated using provided signature, sha256sum and client side bundled RSA public key.
4. If archieve has been successfully validated, it is unziped into `persistent directory`.
5. `revision.json` file is created for further updates checks.
6. Capacitor WebView bridge server assets path is set to current update `persistent directory`

WebView server url persistance is handled by custom Capacitor bridges implementations ([see Setup section](#setup))

### prerequisite

1. generate signing certificates keypair
```
openssl genrsa -out priv_key.key 2048
```

```
openssl rsa -in priv_key.key -pubout -out pub_key.pem
```

2. move generated keys to ./certs directory
```
mv priv_key.key pub_key.pem ./certs
```

### Setup
#### Backend

```
cd backend
```
```
cp .env.template .env
```
```
pip3 install -r ./requirements/common.txt
pip3 install -r ./requirements/deployment.txt
```
```
python3 app.py
```

#### Plugin

```
npm i
```
```
cd slingshot_updater_plugin
```
```
npm run build
```

#### PWA App
```
cd example_app
```
```
npm run build && npx cap sync
```
```
cp ../slingshot_updater_plugin/slingshot.template.json ./android/app/slingshot.json
cp ../certs/pub_key.pem ./android/app/slingshot.pem
```
```
cp ../slingshot_updater_plugin/slingshot.template.json ./ios/App/App/slingshot.json
cp ../certs/pub_key.pem ./ios/App/App/slingshot.pem
```

Custom capacitor bridges have to be plugged into apps entrypoints:
- [ios](example_app/ios/App/App/SceneDelegate.swift)
- [android](example_app/android/app/src/main/java/com/example/app/MainActivity.java)

### shell script for release generation

```
cd example_app
```

```
./create_release.sh 1.0
```

### manual releases + RSA256 signing

1. create release & zip it

```
npm run buid
(cd dist && zip -r ../release.zip .)
```

2. generate release archieve signature using previously generated private key

```
openssl dgst -sha256 -sign priv_key.key -out release.zip.sig release.zip
```

3. calculate sha256 checksum for release archieve

```
openssl dgst -sha256 release.zip > release.zip.sha256
```

4. verify release's signature using public key

```
openssl dgst -sha256 -verify pub_key.pem -signature release.zip.sig release.zip
```

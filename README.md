# slingshot-ota
POC of OTA updater for capacitor.js


### RSA256 release signing
```
openssl rsa -in priv_key.key
openssl pkey -in priv_key.key -pubout -out pub_key.pem
```

```
openssl req -x509 -sha256 -out cert.crt -key priv_key.key -days 365
```

```
npm run buid
zip -r release.zip dist
```

```
openssl dgst -sha256 -sign priv_key.key -out release.zip.sig release.zip
```

```
openssl dgst -sha256 -verify pub_key.pem -signature release.zip.sig release.zip
```

```
openssl dgst -sha256 release.zip > release.zip.sha256
```

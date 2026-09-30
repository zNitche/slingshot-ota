#!/bin/bash

release_version=$1
backend_storage_path="../backend/storage"

echo $release_version

if [ -z $release_version ]; then
    echo "release_version has not been provided"
    exit 1
fi

echo "creating new release $release_version..."

npm run build
(cd dist && zip -r ../release.zip .)

echo "release.zip has been created, signing..."
openssl dgst -sha256 -sign ../certs/priv_key.key -out release.zip.sig release.zip

echo "generating checksum"
openssl dgst -sha256 release.zip > release.zip.sha256

echo "moving files to backend storage"
target_storage_path="$backend_storage_path/$release_version"

read -p "'$target_storage_path' will be removed, press enter to continue"

rm -r "$target_storage_path"
mkdir "$target_storage_path"
mv release.zip "$target_storage_path/release.zip"
mv release.zip.sig "$target_storage_path/release.zip.sig"
mv release.zip.sha256 "$target_storage_path/release.zip.sha256"

echo "done."
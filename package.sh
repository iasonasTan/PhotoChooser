#!/bin/bash
set -e

echo "Removing old files..."
rm -rf packaged

echo "[INFO] You have to run './gradlew jar' before this script!"
mkdir packaged || true >> /dev/null

echo "Copying application jar..."
cp ./app/build/libs/app.jar packaged/PhotoChooser.jar

cd packaged/

echo "Packaging jar into appimage..."
jpackage --input . \
    --main-jar PhotoChooser.jar \
    --icon ../app_icon.png \
    --name PhotoChooser \
    --main-class app.main.Main \
    --type app-image

echo "Zipping output..."
zip -r PhotoChooser.zip PhotoChooser/ >> /dev/null

echo "Removing trash directories..."
rm -rf PhotoChooser

echo "Done!"
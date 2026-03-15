#!/usr/bin/env bash
set -e

echo "Creating temp files..."
mkdir temp || true
cd temp

echo "Extracting libs..."
jar -xf ../libs/Lib.jar

echo "Copying source..."
cd ..
cp -r src/* temp/

echo "Copying resources..."
cp -r resources/* temp

echo "Compiling code..."
cd temp/
javac app/main/Main.java

echo "Packaging into jar..."
JAR_PATH="../out/artifacts/PhotoChooser_jar"
jar --create \
	--file "$JAR_PATH/PhotoChooser.jar" \
	--main-class app.main.Main \
	-C . .

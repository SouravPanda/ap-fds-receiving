#!/usr/bin/env bash

echo "Starting ssh service"
service ssh start
echo "Started ssh service"

echo "Starting Java service"
# Main block to start the jar
JAR_PATH=/app.jar
echo Launching "$JAR_PATH" using JAVA_OPTS="$JAVA_OPTS"
java $JAVA_OPTS -jar "$JAR_PATH"
echo "Started Java Service"
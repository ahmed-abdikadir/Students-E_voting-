#!/bin/bash

# Simple script to run the application

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$PROJECT_DIR/target/E_voting-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found. Building application..."
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests
fi

echo "Starting E-Voting Application..."
echo "Access at: http://localhost:9097"
java -jar "$JAR_FILE"

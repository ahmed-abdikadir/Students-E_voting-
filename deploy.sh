#!/bin/bash

# E-Voting Application Deployment Script

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$PROJECT_DIR/target/E_voting-0.0.1-SNAPSHOT.jar"
LOG_FILE="$PROJECT_DIR/app.log"
PID_FILE="$PROJECT_DIR/app.pid"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}E-Voting Application Deployment${NC}"
echo -e "${GREEN}========================================${NC}"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java is not installed${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep "version" | head -1)
echo -e "${GREEN}Using: $JAVA_VERSION${NC}"

# Step 1: Build the application
echo -e "${YELLOW}Step 1: Building application...${NC}"
cd "$PROJECT_DIR"
mvn clean package -DskipTests

if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}Error: JAR file not found at $JAR_FILE${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Build successful${NC}"

# Step 2: Stop existing application
echo -e "${YELLOW}Step 2: Checking for existing processes...${NC}"
if [ -f "$PID_FILE" ]; then
    OLD_PID=$(cat "$PID_FILE")
    if kill -0 "$OLD_PID" 2>/dev/null; then
        echo "Stopping existing application (PID: $OLD_PID)..."
        kill "$OLD_PID"
        sleep 2
        if kill -0 "$OLD_PID" 2>/dev/null; then
            kill -9 "$OLD_PID"
        fi
    fi
    rm -f "$PID_FILE"
fi

# Step 3: Start the application
echo -e "${YELLOW}Step 3: Starting application...${NC}"
cd "$PROJECT_DIR"
nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
APP_PID=$!
echo $APP_PID > "$PID_FILE"

# Wait for app to start
sleep 3

# Step 4: Verify application is running
echo -e "${YELLOW}Step 4: Verifying application...${NC}"
if kill -0 "$APP_PID" 2>/dev/null; then
    echo -e "${GREEN}✓ Application started successfully (PID: $APP_PID)${NC}"
    echo -e "${GREEN}Application running at: http://localhost:9097${NC}"
else
    echo -e "${RED}Error: Application failed to start${NC}"
    echo "Check logs: tail -f $LOG_FILE"
    exit 1
fi

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Useful commands:"
echo "  View logs:     tail -f $LOG_FILE"
echo "  Stop app:      kill $(cat $PID_FILE)"
echo "  Check status:  curl http://localhost:9097/actuator/health"

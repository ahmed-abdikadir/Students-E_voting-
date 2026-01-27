#!/bin/bash

# E-Voting Application Status Check Script

echo "=========================================="
echo "E-Voting Application Status Check"
echo "=========================================="
echo ""

# Check Java
echo "1. Java Installation:"
if command -v java &> /dev/null; then
    java -version 2>&1 | head -1
    echo "   ✓ Java is installed"
else
    echo "   ✗ Java is NOT installed"
fi
echo ""

# Check Maven
echo "2. Maven Installation:"
if command -v mvn &> /dev/null; then
    mvn -v | head -1
    echo "   ✓ Maven is installed"
else
    echo "   ✗ Maven is NOT installed"
fi
echo ""

# Check Docker
echo "3. Docker Installation:"
if command -v docker &> /dev/null; then
    docker --version
    echo "   ✓ Docker is installed"
else
    echo "   ✗ Docker is NOT installed"
fi
echo ""

# Check Docker Compose
echo "4. Docker Compose Installation:"
if command -v docker-compose &> /dev/null; then
    docker-compose --version
    echo "   ✓ Docker Compose is installed"
else
    echo "   ✗ Docker Compose is NOT installed"
fi
echo ""

# Check MySQL
echo "5. MySQL Connection:"
if command -v mysql &> /dev/null; then
    mysql --version
    echo "   ✓ MySQL client is installed"
else
    echo "   ✗ MySQL client is NOT installed"
fi
echo ""

# Check JAR file
echo "6. Application JAR:"
if [ -f "/Users/Ahmed/Downloads/Students-E_voting-/target/E_voting-0.0.1-SNAPSHOT.jar" ]; then
    echo "   ✓ JAR file exists"
    ls -lh /Users/Ahmed/Downloads/Students-E_voting-/target/E_voting-0.0.1-SNAPSHOT.jar
else
    echo "   ✗ JAR file NOT found (run: mvn clean package -DskipTests)"
fi
echo ""

# Check if app is running
echo "7. Application Running Status:"
if curl -s http://localhost:9097/actuator/health > /dev/null 2>&1; then
    echo "   ✓ Application is running on port 9097"
    curl -s http://localhost:9097/actuator/health | grep -o '"status":"[^"]*"'
else
    echo "   ✗ Application is NOT running on port 9097"
fi
echo ""

# Check Docker containers
echo "8. Docker Containers:"
if command -v docker &> /dev/null; then
    docker ps --format "table {{.Names}}\t{{.Status}}" 2>/dev/null || echo "   (Docker daemon may not be running)"
fi
echo ""

echo "=========================================="
echo "Status Check Complete"
echo "=========================================="

# E-Voting Application Deployment Guide

## Prerequisites
- Java 17 or higher
- MySQL Server running
- Maven (for building)
- Git (optional, for version control)

## Pre-Deployment Checklist

### 1. Database Setup
Ensure MySQL is running and create the database:

```sql
CREATE DATABASE voting_db;
CREATE USER 'e_voting'@'localhost' IDENTIFIED BY 'epassword123!';
GRANT ALL PRIVILEGES ON voting_db.* TO 'e_voting'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration
Edit `src/main/resources/application.properties` with your deployment settings:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/voting_db
spring.datasource.username=e_voting
spring.datasource.password=epassword123!

# Server Configuration
server.port=9097
server.servlet.session.timeout=7200

# Application URL (update for production)
app.base-url=http://localhost:9097
```

## Building the Application

### Option 1: Maven Build (Recommended)
```bash
cd /Users/Ahmed/Downloads/Students-E_voting-
mvn clean package -DskipTests
```

This creates a JAR file at: `target/E_voting-0.0.1-SNAPSHOT.jar`

### Option 2: Maven Build with Tests
```bash
mvn clean package
```

## Running the Application

### Local Development
```bash
java -jar target/E_voting-0.0.1-SNAPSHOT.jar
```

The application will be available at: `http://localhost:9097`

### With Custom Configuration
```bash
java -jar target/E_voting-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://your-host:3306/voting_db \
  --spring.datasource.username=your_user \
  --spring.datasource.password=your_password \
  --server.port=9097
```

### Running in Background (macOS/Linux)
```bash
nohup java -jar target/E_voting-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### Running in Background (Windows)
```cmd
start javaw -jar target/E_voting-0.0.1-SNAPSHOT.jar
```

## Production Deployment

### Using Docker (Recommended)
Create a `Dockerfile` in the project root:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/E_voting-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9097
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t e-voting-app .
docker run -p 9097:9097 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/voting_db \
  -e SPRING_DATASOURCE_USERNAME=e_voting \
  -e SPRING_DATASOURCE_PASSWORD=epassword123! \
  e-voting-app
```

### Using systemd (Linux)
Create `/etc/systemd/system/e-voting.service`:

```ini
[Unit]
Description=E-Voting Application
After=network.target

[Service]
Type=simple
User=voting
ExecStart=/usr/bin/java -jar /path/to/E_voting-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable e-voting
sudo systemctl start e-voting
```

## Monitoring

### Check Application Logs
```bash
tail -f app.log
```

### Health Check Endpoint
```bash
curl http://localhost:9097/actuator/health
```

### Application Metrics
```bash
curl http://localhost:9097/actuator/metrics
```

## Troubleshooting

### Port Already in Use
```bash
# Find process using port 9097
lsof -i :9097

# Kill the process (replace PID)
kill -9 <PID>
```

### Database Connection Issues
- Verify MySQL is running: `mysql -u e_voting -p`
- Check credentials in `application.properties`
- Ensure database exists: `SHOW DATABASES;`

### Out of Memory
Increase JVM memory:
```bash
java -Xmx2G -Xms1G -jar target/E_voting-0.0.1-SNAPSHOT.jar
```

## Security Recommendations

1. **Change Database Password**: Update `epassword123!` to a strong password
2. **Use HTTPS**: Deploy behind a reverse proxy (Nginx/Apache) with SSL
3. **Environment Variables**: Use env vars instead of hardcoding credentials:
   ```bash
   export DB_PASSWORD=your_secure_password
   java -jar E_voting-0.0.1-SNAPSHOT.jar \
     --spring.datasource.password=${DB_PASSWORD}
   ```
4. **Update `app.base-url`**: Set to your production domain
5. **Enable Security Headers**: Configure in Spring Security settings

## Cloud Deployment Options

### AWS
- Deploy JAR to EC2 or use Elastic Beanstalk
- Use AWS RDS for MySQL database
- Configure Security Groups for port 9097

### Azure
- Deploy to Azure App Service
- Use Azure Database for MySQL
- Configure environment variables in App Settings

### Heroku
```bash
git push heroku main
```
(Requires Procfile and environment configuration)

## Rollback Procedure

1. Keep previous JAR versions
2. Stop current application
3. Start with previous JAR version
4. Database: No action needed (Spring maintains schema)

## Support & Logs

Application logs are saved to `app.log`
Archived logs: `app.log.YYYY-MM-DD.X.gz`

Check recent logs:
```bash
tail -n 100 app.log
zcat app.log.2026-01-15.0.gz | tail -n 100
```

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR file
COPY target/E_voting-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 9097

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

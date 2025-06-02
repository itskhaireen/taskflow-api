# Use an official OpenJDK 17 runtime as the base image @ parent image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file (built with JDK 17) into the container
COPY target/taskmanager-0.0.1-SNAPSHOT.jar app.jar

# Expose the Application Port
EXPOSE 8080

# Run the JAR file @ Spring Boot Application
ENTRYPOINT [ "java", "-jar", "app.jar" ]
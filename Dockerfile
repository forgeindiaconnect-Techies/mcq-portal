# ====================================
# Multi-stage Dockerfile for Render
# ====================================

# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render sets the PORT environment variable
EXPOSE 8080

# Activate the render profile
ENV SPRING_PROFILES_ACTIVE=render

ENTRYPOINT ["java", "-jar", "app.jar"]

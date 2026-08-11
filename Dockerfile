# Stage 1: Build the application using Maven with JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build final jar
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Minimal runtime container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/booking_system-0.0.1-SNAPSHOT.jar app.jar

# Render assigns dynamic port via PORT env var (defaults to 8080)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

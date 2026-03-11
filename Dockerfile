FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Final stage - minimal runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create data directory for H2 file database
RUN mkdir -p /app/data

# Copy the built jar file
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables
ENV PORT=8080
ENV DB_PATH=file:/app/data/javamentor

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

# Final stage - minimal runtime image
FROM eclipse-temurin:17-jre-jammy

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

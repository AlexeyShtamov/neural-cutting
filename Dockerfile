# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# Copy Maven wrapper and pom.xml first for better caching
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copy source and build
COPY src src
RUN ./mvnw -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /opt/app

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy JAR from build stage
COPY --from=build /workspace/target/neural-cutting-0.0.1-SNAPSHOT.jar app.jar

# Create uploads directory
RUN mkdir -p /opt/app/uploads && chown -R appuser:appgroup /opt/app

# Switch to non-root user
USER appuser

# Expose port (Render uses PORT env variable)
EXPOSE 8080

# Production-ready JVM options
# -XX:+UseContainerSupport: JVM respects container memory limits
# -XX:MaxRAMPercentage=75.0: Use 75% of container memory for heap
# -XX:+ExitOnOutOfMemoryError: Exit on OOM instead of hanging
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "/opt/app/app.jar"]

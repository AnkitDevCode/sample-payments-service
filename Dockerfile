# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# BEST PRACTICE: Copy dependency files first for better layer caching
# This allows Docker to cache dependencies if only source code changes
COPY pom.xml ./pom.xml
COPY payments-api/pom.xml ./payments-api/pom.xml
COPY payment-security-starter/pom.xml ./payment-security-starter/pom.xml
COPY payments-app/pom.xml ./payments-app/pom.xml

# BEST PRACTICE: Download dependencies separately to leverage caching
RUN mvn -f pom.xml -pl payments-app -am dependency:go-offline -B

# Copy source code after dependencies (changes more frequently)
COPY payments-api ./payments-api
COPY payment-security-starter ./payment-security-starter
COPY payments-app ./payments-app

# BEST PRACTICE: Build with specific flags for reproducibility and security
RUN mvn -f pom.xml -pl payments-app -am clean package -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    --batch-mode \
    --no-transfer-progress

# ==============================================================================
# Stage 2: Runtime
# OPTION 1: Smallest size with distroless (RECOMMENDED for production)
# FROM gcr.io/distroless/java21-debian12
# No shell, no package manager, minimal attack surface (~180MB)
# OPTION 2: Alpine-based JRE (Good balance - currently used)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# BEST PRACTICE: Run as non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring && chown -R spring:spring /app

# BEST PRACTICE: Copy only the built artifact, not the entire build directory
COPY --from=build --chown=spring:spring /app/payments-app/target/*.jar app.jar

# BEST PRACTICE: Switch to non-root user before running application
USER spring:spring

# BEST PRACTICE: Document exposed ports
EXPOSE 8080

# BEST PRACTICE: Add health check for container orchestration
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# BEST PRACTICE: Use ENTRYPOINT with JSON array format for proper signal handling
ENTRYPOINT ["java","-jar","-Dserver.port=8080","/app/app.jar"]

# OPTIONAL: For production, consider adding JVM flags for containerized environments:
# ENTRYPOINT ["java", \
#     "-XX:+UseContainerSupport", \
#     "-XX:MaxRAMPercentage=75.0", \
#     "-XX:InitialRAMPercentage=50.0", \
#     "-XX:+UseG1GC", \
#     "-Djava.security.egd=file:/dev/./urandom", \
#     "-Dserver.port=8080", \
#     "-jar", \
#     "/app/app.jar"]
# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and maven wrapper download dependencies
COPY ./pom.xml ./pom.xml
COPY ./mvnw ./mvnw
COPY ./.mvn ./.mvn
# Make Maven wrapper executable and download dependencies
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copy source files and build
COPY src ./src/

# Build the application
RUN ./mvnw clean package -DskipTests && mv target/*.jar app.jar && rm -rf target

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: non-root user
RUN addgroup -S spring && adduser -S spring -G spring && chown -R spring:spring /app

# Copy the built artifact
COPY --from=build --chown=spring:spring /app/app.jar app.jar

USER spring:spring

# Expose port
EXPOSE 8080

# Health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java","-jar","-Dserver.port=8080","/app/app.jar"]
# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy parent POM and all modules
COPY pom.xml ./pom.xml
COPY payments-api ./payments-api
COPY payment-security-starter ./payment-security-starter
COPY payments-app ./payments-app

# Build only the runnable module using the root POM
RUN mvn -f pom.xml -pl payments-app -am clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring && chown -R spring:spring /app

COPY --from=build --chown=spring:spring /app/payments-app/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","-Dserver.port=8080","/app/app.jar"]

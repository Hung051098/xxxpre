# Dockerfile
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /workspace
COPY pom.xml .
# optionally copy maven settings: COPY settings.xml /root/.m2/settings.xml
RUN mvn -B -DskipTests dependency:go-offline
COPY . .
RUN mvn -B -DskipTests package
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Alpine dùng adduser -D (Don't assign a password)
RUN adduser -D myuser
USER myuser
COPY target/xxxpre-0.0.1-SNAPSHOT.jar app.jar

# 4. Cấu hình lệnh chạy
ENTRYPOINT ["java", "-jar", "app.jar"]
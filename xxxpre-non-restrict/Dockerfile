# Stage 1: Build the application
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /workspace

# 1. Định nghĩa các biến ARG để nhận tham số từ lệnh docker build
ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG NO_PROXY
ARG PROXY_HOST
ARG PROXY_PORT

# 2. Đặt biến môi trường cho các công cụ hệ thống (wget, curl, git, v.v.)
# Lệnh RUN sẽ tự động sử dụng các biến này.
ENV HTTP_PROXY=$HTTP_PROXY
ENV HTTPS_PROXY=$HTTPS_PROXY
ENV NO_PROXY=$NO_PROXY

ENV DB_URL=jdbc:mysql://host.docker.internal:3306/movie?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true&failOverReadOnly=false
ENV DB_USERNAME=root
ENV DB_PASSWORD=your_password
ENV GG_CLIENT_ID=572563808457-10br9242lgdpcvq1ul73ahnc05a3iv1d.apps.googleusercontent.com
ENV GG_CLIENT_SECRET=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
ENV GG_REFRESH_TOKEN=1//04Tka_25G0nz_CgYIARAAGAQSNwF-L9IrfnPfZUIBWh68VFxZt6RhXJiEPJph_jqWjUQVsm1RHzmvxvjqBWLSMtOZMl3OxHOGzl4
ENV PROXY_HOST=10.98.0.196
ENV PROXY_PORT=8000

# 3. Tạo file settings.xml cho Maven để định tuyến kết nối qua Proxy
# Chúng ta sẽ sử dụng tham số -Dmaven.repo.local để buộc Maven dùng settings.xml tùy chỉnh
# Sử dụng 'if' để tránh tạo cấu hình proxy nếu không có proxy nào được truyền vào
RUN if [ -n "$PROXY_HOST" ]; then \
    echo '<settings><proxies><proxy><id>docker-proxy</id><active>true</active><protocol>http</protocol><host>'$PROXY_HOST'</host><port>'$PROXY_PORT'</port></proxy></proxies></settings>' > /usr/share/maven/ref/settings-docker.xml; \
  fi

# 4. Chạy lệnh mvn, sử dụng settings.xml tùy chỉnh nếu nó tồn tại
# Thay thế -DskipTests package bằng lệnh cần thiết của bạn
COPY pom.xml .
# RUN if [ -f "/usr/share/maven/ref/settings-docker.xml" ]; then \
#     mvn -B -DskipTests dependency:go-offline -s /usr/share/maven/ref/settings-docker.xml; \
#   else \
#     mvn -B -DskipTests dependency:go-offline; \
#   fi

COPY src ./src
RUN if [ -f "/usr/share/maven/ref/settings-docker.xml" ]; then \
    mvn -B -DskipTests package -s /usr/share/maven/ref/settings-docker.xml; \
  else \
    mvn -B -DskipTests package; \
  fi

# Stage 2: Run the application
# Use a JRE 21 image (smaller and sufficient for running apps)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security (Alpine syntax)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR file explicitly from the 'build' stage
COPY --from=build /workspace/target/*.jar app.jar

# Configuration to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
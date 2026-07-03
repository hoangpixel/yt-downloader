# 1. Bắt đầu từ một máy tính Linux có cài sẵn Java 21 (Giống bản Java ông đang xài)
FROM eclipse-temurin:21-jdk-jammy

# 2. Cài đặt Python (cần cho yt-dlp) và FFmpeg để ghép hình/tiếng
RUN apt-get update && \
    apt-get install -y ffmpeg python3 python3-pip curl && \
    rm -rf /var/lib/apt/lists/*

# 3. Tải yt-dlp bản chính thức dành cho Linux và cấp quyền chạy cho nó
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

# 4. Tạo thư mục làm việc cho web của ông trong máy tính Linux
WORKDIR /app

# 5. Copy toàn bộ code từ máy tính của ông vào trong Docker
COPY . .

RUN chmod +x mvnw

# 6. Ra lệnh cho Maven build code thành file .jar
RUN ./mvnw clean package -DskipTests

# 7. Copy file .jar vừa build ra thư mục gốc để chuẩn bị chạy
RUN cp target/*.jar app.jar

# 8. Mở cửa số 8080 để khách truy cập
EXPOSE 8080

# 9. Lệnh cuối cùng để khởi động Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
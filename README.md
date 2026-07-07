# YouTube Downloader Service

Dự án này là một ứng dụng web đơn giản và hiệu quả, cho phép người dùng nhập URL YouTube và tải nội dung dưới dạng video hoặc âm thanh thông qua nền tảng Spring Boot và công cụ yt-dlp.

## Tổng quan

Ứng dụng cung cấp trải nghiệm trực quan để:
- xem thông tin cơ bản của video từ YouTube;
- chọn định dạng tải xuống phù hợp;
- theo dõi tiến độ tải;
- tải file về máy ở định dạng mong muốn.

## Tính năng chính

- Giao diện web thân thiện để nhập URL và chọn định dạng.
- Hỗ trợ tải video và audio.
- Theo dõi tiến trình xử lý bằng API riêng.
- Tích hợp với công cụ yt-dlp để xử lý việc tải xuống.

## Công nghệ sử dụng

- Backend: Java, Spring Boot
- Giao diện: Thymeleaf
- Công cụ tải xuống: yt-dlp
- Runtime hỗ trợ: Node.js
- Public service: Ngrok (để mở dịch vụ ra internet khi cần)

## Yêu cầu hệ thống

Trước khi chạy dự án, hãy đảm bảo hệ thống đã cài sẵn:

- JDK 21
- Node.js
- Maven hoặc Maven Wrapper
- Tệp thực thi yt-dlp.exe và ffmpeg.exe phải có mặt trong thư mục tools; nếu thiếu thì ứng dụng sẽ không chạy được.
- Có thể tải ffmpeg.exe tại: https://www.ffmpeg.org/download.html

## Cài đặt và chạy locally

1. Clone repository:
   ```bash
   git clone <repository-url>
   cd yt_downloader
   ```

2. Đảm bảo tệp yt-dlp đã có sẵn trong thư mục tools.

3. Chạy ứng dụng bằng Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Mở trình duyệt và truy cập:
   ```text
   http://localhost:8080
   ```

## Cách sử dụng

- Truy cập trang chủ.
- Dán URL YouTube vào ô nhập.
- Chọn định dạng tải xuống phù hợp.
- Bấm nút tải để bắt đầu quy trình.
- Hệ thống sẽ xử lý và gửi file về cho người dùng.

## Cấu trúc thư mục chính

```text
src/
  main/
    java/
      com/yt_downloader/yt_downloader/
        controller/
        service/
    resources/
      templates/
      static/
      application.properties
```

## Triển khai và public service

Vì YouTube có thể chặn các yêu cầu đến từ IP công cộng, ứng dụng thường được chạy trên máy local và public qua Ngrok khi cần truy cập từ bên ngoài.

### Bước 1: Khởi động ứng dụng

```bash
./mvnw spring-boot:run
```

### Bước 2: Public service bằng Ngrok

1. Tải Ngrok và đăng nhập.
2. Xác thực token:
   ```bash
   ngrok config add-authtoken <your-authtoken>
   ```
3. Chạy lệnh:
   ```bash
   ngrok http 8080
   ```
4. Sử dụng URL được Ngrok cung cấp để truy cập dịch vụ.

## Lưu ý

- Hiệu suất và khả năng tải xuống có thể phụ thuộc vào cấu hình mạng và chính sách của YouTube.
- Đảm bảo bạn có quyền sử dụng nội dung theo quy định pháp luật và điều khoản của nền tảng.

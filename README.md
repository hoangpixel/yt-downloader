# YouTube Downloader Service

Dự án này là hệ thống hỗ trợ tải video từ YouTube, được phát triển dựa trên **Spring Boot** (Backend) kết hợp với công cụ **yt-dlp** để xử lý luồng dữ liệu media.

## Công nghệ sử dụng
- **Backend:** Java, Spring Boot.
- **Download Engine:** [yt-dlp](https://github.com/yt-dlp/yt-dlp).
- **Runtime:** Node.js (cần thiết để giải mã các thách thức JavaScript từ YouTube).
- **Tunneling:** Ngrok (để public service ra internet).

## Cấu trúc hệ thống


## Cách triển khai (Deployment & Hosting)
Vì YouTube thắt chặt bảo mật với các IP Cloud công cộng, hệ thống này được thiết kế để chạy trên máy tính cá nhân (localhost) và sử dụng Ngrok để public service an toàn.

### Bước 1: Khởi động Server
1. Cài đặt [JDK 21](https://adoptium.net/) và [Node.js](https://nodejs.org/).
2. Đảm bảo file `yt-dlp.exe` đã được đặt trong thư mục `/tools`.
3. Chạy ứng dụng Spring Boot bằng lệnh:
   ```bash
   ./mvnw spring-boot:run
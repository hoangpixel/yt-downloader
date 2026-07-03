package com.yt_downloader.yt_downloader.controller;

import com.yt_downloader.yt_downloader.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; // Thêm dòng này

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // --- API ĐÃ ĐƯỢC NÂNG CẤP LÊN TỐC ĐỘ BÀN THỜ ---
    @GetMapping("/api/info")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getVideoInfo(@RequestParam("url") String url) {
        try {
            // Dùng thẳng API chính chủ của YouTube, không xài yt-dlp nữa
            String oEmbedUrl = "https://www.youtube.com/oembed?url=" + url + "&format=json";
            
            // RestTemplate của Spring Boot dùng để gọi API ngoài mạng
            RestTemplate restTemplate = new RestTemplate();
            
            // Lấy kết quả từ YouTube ép vào Map
            Map<String, Object> response = restTemplate.getForObject(oEmbedUrl, Map.class);
            
            if (response != null) {
                Map<String, String> result = new HashMap<>();
                result.put("title", (String) response.get("title"));
                result.put("thumbnail", (String) response.get("thumbnail_url")); // Lấy ảnh xịn JPG
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy thông tin siêu tốc: " + e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/api/progress")
    @ResponseBody
    public ResponseEntity<String> getProgress(@RequestParam("taskId") String taskId) {
        String progress = DownloadService.progressMap.getOrDefault(taskId, "0");
        return ResponseEntity.ok(progress);
    }

    // API CŨ CẦN CẬP NHẬT: Nhận thêm biến taskId từ Frontend
    @PostMapping("/api/download")
    @ResponseBody
    public ResponseEntity<Resource> handleDownload(
            @RequestParam("url") String url,
            @RequestParam("format") String format,
            @RequestParam("taskId") String taskId) { // <-- Nhận thêm biến này
        
        System.out.println("Đang xử lý file: " + url + " | Format: " + format);
        try {
            // Truyền taskId xuống Service
            File downloadedFile = downloadService.downloadVideo(url, format, taskId);
            
            if (downloadedFile == null || !downloadedFile.exists()) {
                return ResponseEntity.internalServerError().build();
            }

            // Code tự động xóa file tạm sau khi gửi xong
            FileInputStream fileInputStream = new FileInputStream(downloadedFile) {
                @Override
                public void close() throws java.io.IOException {
                    super.close();           
                    downloadedFile.delete(); 
                }
            };
            InputStreamResource resource = new InputStreamResource(fileInputStream);

            String tenGoc = downloadedFile.getName().substring(downloadedFile.getName().indexOf("_") + 1);
            String encodedFileName = URLEncoder.encode(tenGoc, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .contentLength(downloadedFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
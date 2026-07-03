package com.yt_downloader.yt_downloader.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class DownloadService {

    public static Map<String, String> progressMap = new ConcurrentHashMap<>();

    public File downloadVideo(String url, String formatId, String taskId) {
        progressMap.put(taskId, "0");
        
        try {
            System.out.println("=== BẮT ĐẦU XỬ LÝ TẢI VIDEO ===");
            
            // 1. TỰ ĐỘNG NHẬN DIỆN HỆ ĐIỀU HÀNH ĐỂ CHỌN ĐÚNG CÔNG CỤ
            String yt_tool;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                yt_tool = "tools/yt-dlp.exe"; // Dành cho lúc ông code ở nhà
            } else {
                yt_tool = "/usr/local/bin/yt-dlp"; // Đường dẫn tuyệt đối trên Render (Linux)
            }
            
            String tmpFolder = System.getProperty("user.dir") + "/tmp_downloads";
            new File(tmpFolder).mkdirs();
            String tienTo = System.currentTimeMillis() + "_";

            List<String> command = new ArrayList<>();
            command.add(yt_tool);
            command.add("--no-playlist");

            if (formatId != null && formatId.startsWith("mp3_")) {
                String bitrate = formatId.split("_")[1]; 
                command.add("-x"); 
                command.add("--audio-format"); command.add("mp3");
                command.add("--audio-quality"); command.add(bitrate + "K"); 
            } else if (formatId != null && formatId.startsWith("mp4_")) {
                String res = formatId.split("_")[1];
                command.add("-f"); 
                command.add("bestvideo[height<=" + res + "]+bestaudio/best");
                if (Integer.parseInt(res) >= 1080) {
                    command.add("-S"); command.add("res,vcodec:h264,acodec:m4a"); 
                } else {
                    command.add("-S"); command.add("vcodec:h264,res,acodec:m4a");
                }
                command.add("--merge-output-format"); command.add("mp4");
            } else {
                command.add("-f"); command.add("bestvideo[height<=720]+bestaudio/best");
                command.add("-S"); command.add("vcodec:h264,res,acodec:m4a");
                command.add("--merge-output-format"); command.add("mp4");
            }

            command.add("-o"); command.add(tmpFolder + "/" + tienTo + "%(title)s.%(ext)s");
            command.add(url);

            System.out.println("Lệnh chuẩn bị chạy: " + String.join(" ", command));

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Giờ thì có log rồi nè
                
                if (line.contains("[download]") && line.contains("%")) {
                    try {
                        String[] parts = line.split("%")[0].split("]");
                        if (parts.length > 1) {
                            String percent = parts[1].trim(); 
                            progressMap.put(taskId, percent);
                        }
                    } catch (Exception ignored) {}
                }
            }

            int exitCode = process.waitFor();
            progressMap.remove(taskId); 
            System.out.println("Tiến trình yt-dlp kết thúc với mã: " + exitCode);

            if (exitCode == 0) {
                File dir = new File(tmpFolder);
                File[] files = dir.listFiles((d, name) -> name.startsWith(tienTo));
                if (files != null && files.length > 0) {
                    System.out.println("Đã tìm thấy file hoàn chỉnh: " + files[0].getName());
                    return files[0];
                }
            } else {
                System.out.println("LỖI: yt-dlp chạy thất bại!");
            }
        } catch (Exception e) {
            System.out.println("==== BẮT ĐƯỢC LỖI RỒI ====");
            e.printStackTrace();
            progressMap.remove(taskId); 
        }
        return null;
    }
}
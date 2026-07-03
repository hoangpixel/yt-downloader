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

    // Bộ nhớ lưu trữ % tiến trình của từng lượt tải
    public static Map<String, String> progressMap = new ConcurrentHashMap<>();

    // Thêm tham số taskId
    public File downloadVideo(String url, String formatId, String taskId) {
        // Khởi tạo tiến trình 0%
        progressMap.put(taskId, "0");
        
        try {
            String yt_tool = "tools/yt-dlp.exe";
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

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); 
                // BÓC TÁCH PHẦN TRĂM TỪ LOG YT-DLP
                if (line.contains("[download]") && line.contains("%")) {
                    try {
                        String[] parts = line.split("%")[0].split("]");
                        if (parts.length > 1) {
                            String percent = parts[1].trim(); // Lấy được con số ví dụ: "45.2"
                            progressMap.put(taskId, percent);
                        }
                    } catch (Exception ignored) {}
                }
            }

            int exitCode = process.waitFor();
            progressMap.remove(taskId); // Tải xong thì dọn dẹp bộ nhớ tiến trình

            if (exitCode == 0) {
                File dir = new File(tmpFolder);
                File[] files = dir.listFiles((d, name) -> name.startsWith(tienTo));
                if (files != null && files.length > 0) {
                    return files[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressMap.remove(taskId); // Lỗi cũng phải dọn dẹp
        }
        return null;
    }
}
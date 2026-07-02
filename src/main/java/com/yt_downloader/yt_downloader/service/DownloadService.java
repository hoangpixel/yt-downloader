package com.yt_downloader.yt_downloader.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.stereotype.Service;

@Service
public class DownloadService {
    public void downloadVideo(String url)
    {
        try
        {
            String yt_tool = "tools/yt-dlp.exe";
            String userHome = System.getProperty("user.home");
            String windowsDownloads = userHome + java.io.File.separator + "Downloads";
           
            // cấu hình lệnh chạy cmd
            ProcessBuilder builder = new ProcessBuilder(
                yt_tool,
                "--no-playlist",
                "-f","best", // lấy chất lượng ok nhất
                "-o", windowsDownloads + "/%(title)s.%(ext)s", // lưu vô folder downloads của win
                url
            );

            // gộp luồng lỗi vào luồng đầu ra để dễ độc log
            builder.redirectErrorStream(true);

            // chạy lệnh
            Process process = builder.start();

            // đọc log để xem % tiến độ
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if(exitCode == 0)
            {
                System.out.println("Tải vid thành công");
            }else
            {
                System.out.println("Tải vid thất bại : " + exitCode);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

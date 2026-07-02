package com.yt_downloader.yt_downloader.controller;
import com.yt_downloader.yt_downloader.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DownloadController {
    @Autowired
    private DownloadService downloadService;

    @GetMapping("/")
    public String homePage()
    {
        return "index";
    }
    
    @PostMapping("/api/download")
    @ResponseBody // này để Spring biết trả về chuỗi chứ kh phải file .html
    public String handleDownload(@RequestParam("url") String url)
    {
        System.out.println("Đã nhận đc link : " + url);
        downloadService.downloadVideo(url);
        return "Yêu cầu tải vid đang được xử lý";
    }
}

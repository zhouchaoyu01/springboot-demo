package com.coding.controller;

import com.coding.service.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-02-19
 */
@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    // 使用构造器注入OcrService
    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
            file.transferTo(convFile);
            String result = ocrService.recognizeText(convFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("识别发生错误：" + e.getMessage());
        }
    }

    @GetMapping("/recognize-url")
    public ResponseEntity<String> recognizeFromUrl(@RequestParam("imageUrl") String imageUrl) {
        try {
            String result = ocrService.recognizeTextFromUrl(imageUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("从URL识别发生错误：" + e.getMessage());
        }
    }
}
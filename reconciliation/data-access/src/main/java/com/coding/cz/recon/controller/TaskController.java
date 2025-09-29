package com.coding.cz.recon.controller;

import com.coding.cz.recon.processor.impl.ApiTaskProcessor;
import com.coding.cz.recon.processor.impl.FileUploadTaskProcessor;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;
import com.coding.cz.recon.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;
    // 这里 TaskService 仍然是构造器注入
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping("/upload/{taskId}")
    public ResponseEntity<?> upload(@PathVariable String taskId, @RequestParam("file") MultipartFile file,
                                    @RequestParam(required = false) String dataDate) throws Exception {
// store file locally (demo)
        Path tmp = Files.createTempFile("upload-", file.getOriginalFilename());
        Files.write(tmp, file.getBytes());
        taskService.runTask(taskId, LocalDate.now(), new HashMap<>() {{ put("filePath", tmp.toString()); }});
        return ResponseEntity.ok("submitted");
    }


    @PostMapping("/run/{taskId}")
    public String run(@PathVariable String taskId) {
        taskService.runTask(taskId, LocalDate.now(), new HashMap<>());
        return "OK";
    }
}
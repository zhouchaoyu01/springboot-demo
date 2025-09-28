package com.coding.cz.recon.controller;

import com.coding.cz.recon.processor.impl.ApiTaskProcessor;
import com.coding.cz.recon.processor.impl.FileUploadTaskProcessor;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;
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


    private final TaskConfigRepository taskConfigRepository;
    private final ParserRuleService parserRuleService;
    private final TaskExecutionLogRepository logRepository;
    private final StandardTransactionService standardTransactionService;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;


    public TaskController(TaskConfigRepository taskConfigRepository, ParserRuleService parserRuleService,
                          TaskExecutionLogRepository logRepository, StandardTransactionService standardTransactionService,
                          ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        this.taskConfigRepository = taskConfigRepository;
        this.parserRuleService = parserRuleService;
        this.logRepository = logRepository;
        this.standardTransactionService = standardTransactionService;
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
    }


    @PostMapping("/upload/{taskId}")
    public ResponseEntity<?> upload(@PathVariable String taskId, @RequestParam("file") MultipartFile file,
                                    @RequestParam(required = false) String dataDate) throws Exception {
// store file locally (demo)
        Path tmp = Files.createTempFile("upload-", file.getOriginalFilename());
        Files.write(tmp, file.getBytes());
        FileUploadTaskProcessor p = new FileUploadTaskProcessor(taskConfigRepository, parserRuleService, logRepository, standardTransactionService);
        p.execute(taskId, LocalDate.parse(dataDate == null ? LocalDate.now().toString() : dataDate), new HashMap<>() {{ put("filePath", tmp.toString()); }});
        return ResponseEntity.ok("submitted");
    }


    @PostMapping("/run/{taskId}")
    public ResponseEntity<?> run(@PathVariable String taskId, @RequestParam(required = false) String date) {
        ApiTaskProcessor p = new ApiTaskProcessor(taskConfigRepository, parserRuleService, logRepository, standardTransactionService,
                objectMapper,
                okHttpClient
                );
        p.execute(taskId, LocalDate.parse(date == null ? LocalDate.now().toString() : date), new HashMap<>());
        return ResponseEntity.ok("started");
    }
}
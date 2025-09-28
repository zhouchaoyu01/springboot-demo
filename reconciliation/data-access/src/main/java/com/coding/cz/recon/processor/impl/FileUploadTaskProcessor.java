package com.coding.cz.recon.processor.impl;

import com.coding.cz.recon.entity.TaskConfig;
import com.coding.cz.recon.processor.AbstractTaskProcessor;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public class FileUploadTaskProcessor extends AbstractTaskProcessor {


    public FileUploadTaskProcessor(TaskConfigRepository taskConfigRepository, ParserRuleService parserRuleService,
                                   TaskExecutionLogRepository logRepository, StandardTransactionService standardTransactionService) {
        super(taskConfigRepository, parserRuleService, logRepository, standardTransactionService);
    }


    @Override
    protected byte[] fetchData(String taskId, Map<String, Object> runtimeParams) throws Exception {
// runtimeParams should contain filePath
        String path = (String) runtimeParams.get("filePath");
        if (path == null) {
            TaskConfig cfg = this.taskConfigRepository.findById(taskId).orElseThrow();
// fallback to fetchConfig
            path = cfg.getFetchConfig();
        }
        return Files.readAllBytes(Path.of(path));
    }
}
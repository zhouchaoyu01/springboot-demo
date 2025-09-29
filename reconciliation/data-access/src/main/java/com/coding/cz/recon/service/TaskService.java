package com.coding.cz.recon.service;

import com.coding.cz.recon.entity.TaskConfig;
import com.coding.cz.recon.processor.AbstractTaskProcessor;
import com.coding.cz.recon.processor.TaskProcessorFactory;
import com.coding.cz.recon.repository.TaskConfigRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-29
 */
@Service
public class TaskService {

    private final TaskConfigRepository taskConfigRepository;
    private final TaskProcessorFactory processorFactory;

    public TaskService(TaskConfigRepository taskConfigRepository,
                       TaskProcessorFactory processorFactory) {
        this.taskConfigRepository = taskConfigRepository;
        this.processorFactory = processorFactory;
    }

    public void runTask(String taskId, LocalDate dataDate, Map<String,Object> runtimeParams) {
        TaskConfig cfg = taskConfigRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + taskId));

        AbstractTaskProcessor processor = processorFactory.getProcessor(cfg.getFetchMode());
        processor.execute(taskId, dataDate, runtimeParams);
    }
}


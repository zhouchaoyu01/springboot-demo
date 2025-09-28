package com.coding.cz.recon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Data
@Entity
@Table(name = "task_execution_log")
public class TaskExecutionLog {
    @Id
    @GeneratedValue
    private Long executionId;
    private String taskId;
    private LocalDate dataDate;
    private String fileName;
    private String fetchStatus;
    private String parseStatus;
    private Integer recordCount;
    private LocalDateTime execTime;
    private String lastError;
}
package com.coding.cz.recon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Data
@Entity
@Table(name = "task_config")
public class TaskConfig {
    @Id
    private String taskId;
    private String name;
    private String channelId;
    private String dataType; // CLEARING/SETTLEMENT/PLATFORM_PAYMENT
    private String fetchMode; // API/UPLOAD/SQL/MQ
    private String dataForm; // FILE/STREAM
    @Column(columnDefinition = "json")
    private String fetchConfig; // JSON
    private String parserRuleId;
}
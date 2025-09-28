package com.coding.cz.recon.repository;

import com.coding.cz.recon.entity.TaskExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public interface TaskExecutionLogRepository extends JpaRepository<TaskExecutionLog, Long> {
}
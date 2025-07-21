package com.coding.controller;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-05-08
 */
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CsvWriter {
    private final BlockingQueue<String> recordQueue = new LinkedBlockingQueue<>(10000);
    private volatile boolean isRunning = true;
    private BufferedWriter writer;

    @PostConstruct
    public void init() throws IOException {
        // 初始化CSV文件并写入表头
        Path filePath = Paths.get("reconciliation_errors.csv");
        writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        writer.write("DIFF_TYPE,LOCAL_ORDER_ID,CHANNEL_ORDER_ID,AMOUNT_DIFF,REASON\n");
        writer.flush();

        // 启动消费者线程
        Thread consumerThread = new Thread(this::writeToCsv);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public void addRecord(String record) {
        try {
            recordQueue.put(record);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void writeToCsv() {
        try {
            while (isRunning || !recordQueue.isEmpty()) {
                String record = recordQueue.poll();
                if (record != null) {
                    writer.write(record);
                    writer.newLine();
                    // 每100条刷新一次缓冲区
                    if (recordQueue.size() % 100 == 0) {
                        writer.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        isRunning = false;
    }
}
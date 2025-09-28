package com.coding.cz.recon.processor.impl;

import com.coding.cz.recon.entity.TaskConfig;
import com.coding.cz.recon.processor.AbstractTaskProcessor;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public class ApiTaskProcessor extends AbstractTaskProcessor {

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final RestTemplate restTemplate = new RestTemplate();


    public ApiTaskProcessor(TaskConfigRepository taskConfigRepository, ParserRuleService parserRuleService,
                            TaskExecutionLogRepository logRepository, StandardTransactionService standardTransactionService,
                            ObjectMapper objectMapper,
                            OkHttpClient okHttpClient
                            ) {
        super(taskConfigRepository, parserRuleService, logRepository, standardTransactionService);
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;

    }


    @Override
    protected byte[] fetchData(String taskId, Map<String, Object> runtimeParams) throws Exception {
        TaskConfig cfg = this.taskConfigRepository.findById(taskId).orElseThrow();
//// fetchConfig sample {"url":"http://.../download"}
//        String json = cfg.getFetchConfig();
//// basic parsing - in real use, use Jackson to parse fetchConfig
//        String url = json.contains("url") ? json.substring(json.indexOf("url")+6).split("")[0] : null;
//        String resp = restTemplate.getForObject(url, String.class);
        // 解析 fetchConfig
        JsonNode node = objectMapper.readTree(cfg.getFetchConfig());
        String url = node.path("url").asText();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("fetchConfig 中未配置 url");
        }
        // 调用 API
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("调用API失败: " + response.code() + " - " + response.message());
            }
            String resp = response.body().string();
            return resp.getBytes(StandardCharsets.UTF_8);
        }
    }

}

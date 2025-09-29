package com.coding.cz.recon.processor.impl;

import com.coding.cz.recon.entity.TaskConfig;
import com.coding.cz.recon.processor.AbstractTaskProcessor;
import com.coding.cz.recon.processor.DynamicSqlExecutor;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;
import com.coding.cz.recon.util.DataTransformer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Component
@Qualifier("API")      // fetchMode = API
public class ApiTaskProcessor extends AbstractTaskProcessor {

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public ApiTaskProcessor(TaskConfigRepository taskConfigRepository,
                            ParserRuleService parserRuleService,
                            TaskExecutionLogRepository logRepository,
                            DynamicSqlExecutor dynamicSqlExecutor,
                            OkHttpClient okHttpClient,
                            ObjectMapper objectMapper) {
        super(taskConfigRepository, parserRuleService, logRepository, dynamicSqlExecutor);
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }




    @Override
    protected byte[] fetchData(String taskId, Map<String, Object> runtimeParams) throws Exception {
        TaskConfig cfg = this.taskConfigRepository.findById(taskId).orElseThrow();
        // fetchConfig sample {"url":"http://.../download"}
        // 解析 fetchConfig
        JsonNode node = objectMapper.readTree(cfg.getFetchConfig());
        String url = node.path("url").asText();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("fetchConfig 中未配置 url");
        }
        // 1. 从 runtimeParams 取参数（这里只演示 date）
        String date = (String) runtimeParams.get("date");
        if (date == null) {
            throw new IllegalArgumentException("缺少必要参数: date");
        }

        // 2. 构造 JSON body
        ObjectNode body = objectMapper.createObjectNode();
        body.put("date", date);
        RequestBody requestBody = RequestBody.create(
                body.toString(),
                MediaType.parse("application/json")
        );
        // 调用 API
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
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

package com.coding.cz.recon.processor;

import com.coding.cz.recon.entity.*;
import com.coding.cz.recon.parser.DataParser;
import com.coding.cz.recon.parser.ParserFactory;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;
import com.coding.cz.recon.util.DataTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */

public abstract class AbstractTaskProcessor {


    protected final TaskConfigRepository taskConfigRepository;
    protected final ParserRuleService parserRuleService;
    protected final TaskExecutionLogRepository logRepository;
    protected final DynamicSqlExecutor dynamicSqlExecutor;
//    protected final StandardTransactionService standardTransactionService;



//    public AbstractTaskProcessor(TaskConfigRepository taskConfigRepository, ParserRuleService parserRuleService,
//                                 TaskExecutionLogRepository logRepository, StandardTransactionService standardTransactionService) {
//        this.taskConfigRepository = taskConfigRepository;
//        this.parserRuleService = parserRuleService;
//        this.logRepository = logRepository;
//        this.standardTransactionService = standardTransactionService;
//    }
public AbstractTaskProcessor(TaskConfigRepository taskConfigRepository,
                             ParserRuleService parserRuleService,
                             TaskExecutionLogRepository logRepository,
                             DynamicSqlExecutor dynamicSqlExecutor
                             ) {
    this.taskConfigRepository = taskConfigRepository;
    this.parserRuleService = parserRuleService;
    this.logRepository = logRepository;
    this.dynamicSqlExecutor = dynamicSqlExecutor;

}


    public void execute(String taskId, LocalDate dataDate, Map<String,Object> runtimeParams) {
        TaskExecutionLog log = new TaskExecutionLog();
        log.setTaskId(taskId);
        log.setDataDate(dataDate);
        log.setExecTime(LocalDateTime.now());
        log.setFetchStatus("PENDING");
        log.setParseStatus("PENDING");
        logRepository.save(log);


        try {
            // 1. 数据获取
            byte[] raw = fetchData(taskId, runtimeParams);
            log.setFetchStatus("SUCCESS");
            logRepository.save(log);

            // 2. 加载任务 & 规则配置
            TaskConfig cfg = taskConfigRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("任务不存在: " + taskId));
            ParserRule rule = parserRuleService.loadRule(cfg.getParserRuleId());
            List<ParserRuleDetail> details = parserRuleService.loadRuleDetails(rule.getParserRuleId());

            // 3. 调用解析器
            DataParser parser = ParserFactory.getParser(rule, details);
            List<Map<String,String>> rows = parser.parse(raw);


            // 4. 清洗 + 动态入库
//            List<StandardTransaction> txs = transformToStandard(rows, cfg, rule, details);
//            standardTransactionService.saveBatch(txs);
            List<Map<String,Object>> toInsert = new ArrayList<>();
            for (Map<String,String> row : rows) {
                Map<String,Object> mapped = new LinkedHashMap<>();
                for (ParserRuleDetail d : details) {
                    String rawVal = row.get(d.getTargetField());
                    Object val = DataTransformer.transform(rawVal, d);

                    if (Boolean.TRUE.equals(d.getIsRequired()) && val == null) {
                        throw new IllegalArgumentException("字段 " + d.getTargetField() + " 缺失");
                    }
                    mapped.put(d.getTargetField(), val);
                }
                toInsert.add(mapped);
            }

            // 动态 SQL 入库
            dynamicSqlExecutor.batchInsert(rule.getTargetTable(), toInsert);

            // 5. 更新日志
            log.setParseStatus("SUCCESS");
            log.setRecordCount(rows.size());
            logRepository.save(log);
        } catch (Exception ex) {
            log.setFetchStatus("FAILED");
            log.setParseStatus("FAILED");
            log.setLastError(ex.getMessage());
            logRepository.save(log);
            throw new RuntimeException(ex);
        }
    }


    protected abstract byte[] fetchData(String taskId, Map<String,Object> runtimeParams) throws Exception;

    private String cleanCellValue(String raw) {
        if (raw == null) return null;
        // 去掉前后空格，并去掉开头的 ` 或 '
        return raw.trim().replaceAll("^[`']+", "");
    }

    protected List<StandardTransaction> transformToStandard(List<Map<String,String>> rows, TaskConfig cfg, ParserRule rule, List<ParserRuleDetail> details) throws JsonProcessingException {
        List<StandardTransaction> list = new ArrayList<>();
        for (Map<String,String> row : rows) {
            StandardTransaction tx = new StandardTransaction();
            tx.setBizDate(LocalDate.now()); // or parse from row if available
            tx.setChannel(cfg.getChannelId());
            tx.setFundAccountId(null); // fill if mapping available
            for (ParserRuleDetail d : details) {
                String val = row.get(d.getTargetField());
                if (val == null) val = d.getDefaultValue();
                val = cleanCellValue(val);
                switch (d.getTargetField()) {
                    case "trade_time":
                        if (val != null) tx.setTradeTime(LocalDateTime.parse(val, DateTimeFormatter.ofPattern(d.getFormatExpr())));
                        break;
                    case "order_id": tx.setOrderId(val); break;
                    case "channel_txn_id": tx.setChannelTxnId(val); break;
                    case "amount":
                        if (val != null) {
                            BigDecimal amt = new BigDecimal(val);
                            if ("元".equals(d.getUnit())) amt = amt.multiply(BigDecimal.valueOf(100));
                            tx.setAmount(amt);
                        }
                        break;
                    case "fee":
                        if (val != null){
                            BigDecimal amt = new BigDecimal(val);
                            if ("元".equals(d.getUnit())) amt = amt.multiply(BigDecimal.valueOf(100));
                            tx.setFee(amt);
                        }
                        break;
                    case "status":
                        if (d.getEnumMapping() != null) {
                            Map<String,String> map = new ObjectMapper().readValue(d.getEnumMapping(), new TypeReference<>(){});
                            tx.setStatus(map.getOrDefault(val, val));
                        } else {
                            tx.setStatus(val);
                        }
                        break;
                    case "biz_type": tx.setBizType(val); break;
                }
            }
// keep raw row JSON for traceability
            tx.setRawSource(new ObjectMapper().writeValueAsString(row));
            list.add(tx);
        }
        return list;
    }
}
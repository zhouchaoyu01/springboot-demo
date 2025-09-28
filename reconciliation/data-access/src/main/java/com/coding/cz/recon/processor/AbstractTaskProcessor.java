package com.coding.cz.recon.processor;

import com.coding.cz.recon.entity.*;
import com.coding.cz.recon.parser.DataParser;
import com.coding.cz.recon.parser.ParserFactory;
import com.coding.cz.recon.repository.TaskConfigRepository;
import com.coding.cz.recon.repository.TaskExecutionLogRepository;
import com.coding.cz.recon.service.ParserRuleService;
import com.coding.cz.recon.service.StandardTransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    protected final StandardTransactionService standardTransactionService;


    public AbstractTaskProcessor(TaskConfigRepository taskConfigRepository, ParserRuleService parserRuleService,
                                 TaskExecutionLogRepository logRepository, StandardTransactionService standardTransactionService) {
        this.taskConfigRepository = taskConfigRepository;
        this.parserRuleService = parserRuleService;
        this.logRepository = logRepository;
        this.standardTransactionService = standardTransactionService;
    }


    @Transactional
    public void execute(String taskId, LocalDate dataDate, Map<String,Object> runtimeParams) {
        TaskExecutionLog log = new TaskExecutionLog();
        log.setTaskId(taskId);
        log.setDataDate(dataDate);
        log.setExecTime(LocalDateTime.now());
        log.setFetchStatus("PENDING");
        log.setParseStatus("PENDING");
        logRepository.save(log);


        try {
            byte[] raw = fetchData(taskId, runtimeParams);
            log.setFetchStatus("SUCCESS");
            logRepository.save(log);


            TaskConfig cfg = taskConfigRepository.findById(taskId).orElseThrow();
            ParserRule rule = parserRuleService.loadRule(cfg.getParserRuleId());
            List<ParserRuleDetail> details = parserRuleService.loadRuleDetails(rule.getParserRuleId());


            DataParser parser = ParserFactory.getParser(rule, details);
            List<Map<String,String>> rows = parser.parse(raw);


// transform rows -> standard DTOs and persist
// TODO: implement transformation & batch save

            List<StandardTransaction> txs = transformToStandard(rows, cfg, rule, details);
            standardTransactionService.saveBatch(txs);

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
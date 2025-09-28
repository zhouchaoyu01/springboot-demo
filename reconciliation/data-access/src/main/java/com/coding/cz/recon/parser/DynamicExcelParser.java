package com.coding.cz.recon.parser;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.coding.cz.recon.entity.ParserRule;
import com.coding.cz.recon.entity.ParserRuleDetail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public class DynamicExcelParser implements DataParser {


    private final ParserRule rule;
    private final List<ParserRuleDetail> details;


    public DynamicExcelParser(ParserRule rule, List<ParserRuleDetail> details) {
        this.rule = rule;
        this.details = details;
    }


    @Override
    public List<Map<String, String>> parse(byte[] raw) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        try (InputStream is = new ByteArrayInputStream(raw)) {
            EasyExcel.read(is, new AnalysisEventListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> row, AnalysisContext context) {
                    int rowIndex = context.readRowHolder().getRowIndex();
                    if (rule.getStartRow() != null && rowIndex + 1 < rule.getStartRow()) {
                        return; // skip header
                    }
                    Map<String, String> mapped = new HashMap<>();
                    for (ParserRuleDetail d : details) {
                        Integer idx = d.getSourceColIndex();
                        String rawVal = null;
                        if (idx != null && idx >= 0 && idx < row.size()) rawVal = row.get(idx);
                        mapped.put(d.getTargetField(), rawVal);
                    }
                    rows.add(mapped);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().doRead();

            // 跳过尾部统计/签名行
            int n = rule.getSkipTailRows() == null ? 0 : rule.getSkipTailRows();
            if (n > 0 && rows.size() > n) {
                return new ArrayList<>(rows.subList(0, rows.size() - n));
            }

            return rows;
        }
    }
}
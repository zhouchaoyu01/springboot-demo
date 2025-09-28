package com.coding.cz.recon.parser;

import com.coding.cz.recon.entity.ParserRule;
import com.coding.cz.recon.entity.ParserRuleDetail;

import java.util.List;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public class ParserFactory {
    public static DataParser getParser(ParserRule rule, List<ParserRuleDetail> details) {
        String fileType = rule.getFileType();
        if (fileType == null) fileType = "EXCEL";
        switch (fileType.toUpperCase()) {
            case "EXCEL":
            case "XLSX":
                return new DynamicExcelParser(rule, details);
            case "CSV":
// reuse the excel parser by setting CSV type or implement DynamicCsvParser
                return new DynamicExcelParser(rule, details);
            case "JSON":
                return new DynamicJsonParser(rule, details);
            default:
                throw new IllegalArgumentException("Unsupported fileType:" + fileType);
        }
    }
}

package com.coding.cz.recon.parser;

import com.coding.cz.recon.entity.ParserRule;
import com.coding.cz.recon.entity.ParserRuleDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public class DynamicJsonParser implements DataParser {
    private final ParserRule rule;
    private final List<ParserRuleDetail> details;
    private final ObjectMapper mapper = new ObjectMapper();


    public DynamicJsonParser(ParserRule rule, List<ParserRuleDetail> details) {
        this.rule = rule;
        this.details = details;
    }


    @Override
    public List<Map<String, String>> parse(byte[] raw) throws Exception {
        List<Map<String, Object>> source = mapper.readValue(raw, new TypeReference<>(){});
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, Object> r : source) {
        // map by detail.sourceColName or sourceColIndex
            Map<String, String> mapped = mapper.convertValue(r, new TypeReference<>(){});
            result.add(mapped);
        }
        return result;
    }
}
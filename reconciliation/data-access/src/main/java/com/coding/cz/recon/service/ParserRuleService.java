package com.coding.cz.recon.service;

import com.coding.cz.recon.entity.ParserRule;
import com.coding.cz.recon.entity.ParserRuleDetail;
import com.coding.cz.recon.repository.ParserRuleDetailRepository;
import com.coding.cz.recon.repository.ParserRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Service
public class ParserRuleService {


    private final ParserRuleRepository ruleRepo;
    private final ParserRuleDetailRepository detailRepo;


    public ParserRuleService(ParserRuleRepository ruleRepo, ParserRuleDetailRepository detailRepo) {
        this.ruleRepo = ruleRepo;
        this.detailRepo = detailRepo;
    }


    public ParserRule loadRule(String ruleId) {
        return ruleRepo.findById(ruleId).orElse(null);
    }


    public List<ParserRuleDetail> loadRuleDetails(String ruleId) {
        return detailRepo.findByParserRuleIdOrderBySourceColIndex(ruleId);
    }
}
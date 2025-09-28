package com.coding.cz.recon.repository;

import com.coding.cz.recon.entity.ParserRuleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
public interface ParserRuleDetailRepository extends JpaRepository<ParserRuleDetail, Long> {
    List<ParserRuleDetail> findByParserRuleIdOrderBySourceColIndex(String parserRuleId);
}

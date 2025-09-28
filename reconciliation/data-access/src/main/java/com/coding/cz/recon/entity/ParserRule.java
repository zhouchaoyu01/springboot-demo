package com.coding.cz.recon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Data
@Entity
@Table(name = "parser_rule")
public class ParserRule {
    @Id
    private String parserRuleId;
    private String institution;
    private String dataType;
    private String fetchMode;
    private String fileType; // EXCEL/CSV/JSON
    private String targetTable;
    private Integer startRow;
    private String delimiter;
    private String encoding;
    private Integer skipTailRows;   // 跳过尾部行数
}

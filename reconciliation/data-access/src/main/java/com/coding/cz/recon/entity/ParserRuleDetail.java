package com.coding.cz.recon.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Data
@Entity
@Table(name = "parser_rule_detail")
public class ParserRuleDetail {
    @Id
    @GeneratedValue
    private Long id;
    private String parserRuleId;
    private Integer sourceColIndex; // 0-based
    private String sourceColName;
    private String targetField; // trade_time, order_id, amount...
    private String dataType; // string/int/decimal/date/enum
    private String formatExpr;
    private String unit;
    private String defaultValue;
    @Column(columnDefinition = "json")
    private String enumMapping;
    private Boolean isRequired;
}

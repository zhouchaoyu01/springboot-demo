package com.coding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-05-06
 */
@Data
@AllArgsConstructor
public class LocalTransaction {
    private String orderId;      // 订单号
    private BigDecimal amount;   // 金额
    private String status;       // 状态（SUCCESS/FAIL）
    // 其他字段：渠道类型、交易时间等
}
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
// 渠道对账文件记录
public class ChannelTransaction {
    private String channelOrderId; // 渠道订单号
    private String orderId;         // 业务订单号（与LocalTransaction关联）
    private BigDecimal amount;
    private String channelStatus;   // 渠道状态码（需与本地状态映射）
    // 其他字段：手续费、交易完成时间等
}
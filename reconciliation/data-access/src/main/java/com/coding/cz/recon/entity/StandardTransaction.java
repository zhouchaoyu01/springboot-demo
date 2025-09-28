package com.coding.cz.recon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Data
@Entity
@Table(name = "standard_transaction")
public class StandardTransaction {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate bizDate;
    private String merchantId;
    private String channel;
    private String fundAccountId;
    private String orderId;
    private String channelTxnId;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime tradeTime;
    private String status;
    private String bizType;
    private String rawSource; // json
}
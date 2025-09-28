package com.coding.cz.recon.service;

import com.coding.cz.recon.entity.StandardTransaction;
import com.coding.cz.recon.repository.StandardTransactionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-26
 */
@Service
public class StandardTransactionService {


    private final StandardTransactionRepository repo;
    private final JdbcTemplate jdbcTemplate;


    public StandardTransactionService(StandardTransactionRepository repo, JdbcTemplate jdbcTemplate) {
        this.repo = repo;
        this.jdbcTemplate = jdbcTemplate;
    }


    public void saveBatch(List<StandardTransaction> list) {
        if (list.isEmpty()) return;
        String sql = "INSERT INTO standard_transaction (biz_date, merchant_id, channel, fund_account_id, order_id, channel_txn_id, amount, fee, trade_time, status, biz_type, raw_source) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount=VALUES(amount), fee=VALUES(fee), trade_time=VALUES(trade_time), status=VALUES(status), raw_source=VALUES(raw_source)";


        jdbcTemplate.batchUpdate(sql, list, 1000, (ps, tx) -> {
            ps.setObject(1, tx.getBizDate());
            ps.setString(2, tx.getMerchantId());
            ps.setString(3, tx.getChannel());
            ps.setString(4, tx.getFundAccountId());
            ps.setString(5, tx.getOrderId());
            ps.setString(6, tx.getChannelTxnId());
            ps.setBigDecimal(7, tx.getAmount());
            ps.setBigDecimal(8, tx.getFee());
            ps.setObject(9, tx.getTradeTime());
            ps.setString(10, tx.getStatus());
            ps.setString(11, tx.getBizType());
            ps.setString(12, tx.getRawSource());
        });
    }
}
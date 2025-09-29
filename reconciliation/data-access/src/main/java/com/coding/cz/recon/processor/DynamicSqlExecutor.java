package com.coding.cz.recon.processor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-29
 */
@Component
public class DynamicSqlExecutor {

    private final JdbcTemplate jdbcTemplate;

    public DynamicSqlExecutor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(String tableName, List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return;

        // 提取列名
        List<String> columns = new ArrayList<>(rows.get(0).keySet());
        String columnSql = String.join(",", columns);
        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(","));

        String sql = "INSERT INTO " + tableName + " (" + columnSql + ") VALUES (" + placeholders + ")";

        jdbcTemplate.batchUpdate(sql, rows, 1000, (ps, row) -> {
            for (int i = 0; i < columns.size(); i++) {
                ps.setObject(i + 1, row.get(columns.get(i)));
            }
        });
    }
}


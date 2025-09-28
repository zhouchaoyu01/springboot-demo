-- 1. 支付渠道配置
CREATE TABLE channel (
                         channel_id VARCHAR(32) PRIMARY KEY, -- e.g. WX, ALI
                         channel_name VARCHAR(128) NOT NULL,
                         channel_type VARCHAR(32) NOT NULL, -- bank/third/fourth
                         status VARCHAR(16) DEFAULT 'ENABLED',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 资金账户配置
CREATE TABLE fund_account (
                              account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              channel_id VARCHAR(32) NOT NULL,
                              institution_type VARCHAR(32) NOT NULL, -- BANK/THIRD/FORTH
                              account_no VARCHAR(64) NOT NULL,
                              account_attr VARCHAR(32), -- BASIC/GENERAL/PAYMENT
                              settlement_mode VARCHAR(32), -- T+0/T+1/D+0
                              start_date DATE,
                              opening_balance DECIMAL(18,2) DEFAULT 0,
                              status VARCHAR(16) DEFAULT 'ENABLED',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              UNIQUE KEY uq_channel_account (channel_id, account_no),
                              FOREIGN KEY (channel_id) REFERENCES channel(channel_id)
);

-- 3. 任务配置
CREATE TABLE task_config (
                             task_id VARCHAR(64) PRIMARY KEY,
                             name VARCHAR(128),
                             channel_id VARCHAR(32),
                             data_type VARCHAR(32), -- CLEARING/SETTLEMENT/PLATFORM_PAYMENT/PLATFORM_SERVICE
                             fetch_mode VARCHAR(32), -- API/SQL/MQ/UPLOAD
                             data_form VARCHAR(16),  -- FILE/STREAM
                             schedule_time TIME NULL,
                             fetch_config JSON,      -- e.g. { "url":"...", "headers": {...} } or { "filePath": "..." }
                             parser_rule_id VARCHAR(64),
                             status VARCHAR(16) DEFAULT 'ENABLED',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 任务执行日志
CREATE TABLE task_execution_log (
                                    execution_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    task_id VARCHAR(64) NOT NULL,
                                    data_date DATE,
                                    file_name VARCHAR(256),
                                    fetch_status VARCHAR(32) DEFAULT 'PENDING', -- PENDING/SUCCESS/FAILED
                                    parse_status VARCHAR(32) DEFAULT 'PENDING', -- PENDING/SUCCESS/FAILED
                                    record_count INT DEFAULT 0,
                                    exec_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    last_error TEXT,
                                    UNIQUE KEY uq_task_date (task_id, data_date)
);

-- 5. 解析规则头
CREATE TABLE parser_rule (
                             parser_rule_id VARCHAR(64) PRIMARY KEY,
                             institution VARCHAR(64), -- WX/ALI/BANK/PLATFORM
                             data_type VARCHAR(32),   -- CLEARING/SETTLEMENT/...
                             fetch_mode VARCHAR(32),  -- API/UPLOAD/SQL/MQ
                             file_type VARCHAR(32),   -- EXCEL/CSV/TXT/JSON
                             target_table VARCHAR(64), -- e.g. clearing_record
                             start_row INT DEFAULT 1,
                             delimiter VARCHAR(8),
                             encoding VARCHAR(32) DEFAULT 'UTF-8',
                             status VARCHAR(16) DEFAULT 'ENABLED',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. 解析规则明细（列映射 + 转换）
CREATE TABLE parser_rule_detail (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    parser_rule_id VARCHAR(64) NOT NULL,
                                    source_col_index INT,               -- Excel列索引(0-based)，或 -1 表示按 header 名称匹配
                                    source_col_name VARCHAR(128),       -- 表头名，可选
                                    target_field VARCHAR(64) NOT NULL,  -- 统一字段名，如 trade_time, order_id, amount
                                    data_type VARCHAR(32) DEFAULT 'string', -- string/int/decimal/date/enum
                                    format_expr VARCHAR(128),           -- 日期格式 or transform expression e.g. yyyy-MM-dd HH:mm:ss
                                    unit VARCHAR(16),                   -- 单位标识，如 元 / 分
                                    default_value VARCHAR(128),
                                    enum_mapping JSON,                  -- {"成功":"SUCCESS","退款":"REFUND"} 可空
                                    is_required TINYINT DEFAULT 0,
                                    UNIQUE KEY uq_rule_col (parser_rule_id, source_col_index)
);

-- 7. 清算数据表 (逐笔)
CREATE TABLE clearing_record (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 merchant_id VARCHAR(64),
                                 clearing_date DATE,
                                 fund_account_id VARCHAR(64),
                                 settlement_date DATE,
                                 channel_order_id VARCHAR(128),
                                 merchant_order_id VARCHAR(128),
                                 channel VARCHAR(32),
                                 service_name VARCHAR(128),
                                 biz_type VARCHAR(32),
                                 amount DECIMAL(18,2),
                                 pay_success_time DATETIME,
                                 external_trace_no VARCHAR(128),
                                 raw_payload JSON, -- 原始行的 json/text 以便审计
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE KEY uq_channel_order (channel, channel_order_id)
);

-- 8. 结算数据表 (资金维度)
CREATE TABLE settlement_record (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   merchant_id VARCHAR(64),
                                   fund_account_id VARCHAR(64),
                                   settle_date DATE,
                                   bank_account_no VARCHAR(64),
                                   service_order_id VARCHAR(128),
                                   biz_type VARCHAR(32),
                                   income_expense VARCHAR(16), -- INCOME/EXPENSE
                                   amount DECIMAL(18,2),
                                   settle_time DATETIME,
                                   raw_payload JSON,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. 标准化交易表（所有渠道数据映射后的统一表）
CREATE TABLE standard_transaction (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      biz_date DATE,
                                      merchant_id VARCHAR(64),
                                      channel VARCHAR(32),
                                      fund_account_id VARCHAR(64),
                                      order_id VARCHAR(128),
                                      channel_txn_id VARCHAR(128),
                                      amount DECIMAL(18,2),
                                      fee DECIMAL(18,2),
                                      trade_time DATETIME,
                                      status VARCHAR(32),
                                      biz_type VARCHAR(32),
                                      raw_source JSON,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      UNIQUE KEY uq_std (biz_date, merchant_id, channel, order_id, channel_txn_id)
);



















INSERT INTO parser_rule (
    parser_rule_id, institution, data_type, fetch_mode, file_type, target_table, start_row, delimiter, encoding, status
) VALUES (
             'WX_CLEARING_EXCEL',
             'WX',                 -- 微信
             'CLEARING',           -- 清算数据
             'UPLOAD',             -- 上传模式
             'EXCEL',
             'standard_transaction',
             2,                    -- 跳过表头，从第2行开始解析
             NULL,
             'UTF-8',
             'ENABLED'
         );


INSERT INTO parser_rule_detail (parser_rule_id, source_col_index, source_col_name, target_field, data_type, format_expr, unit, enum_mapping, is_required)
VALUES
    ('WX_CLEARING_EXCEL', 0, '交易时间', 'trade_time', 'date', 'yyyy-MM-dd HH:mm:ss', NULL, NULL, 1),
    ('WX_CLEARING_EXCEL', 6, '商户订单号', 'order_id', 'string', NULL, NULL, NULL, 1),
    ('WX_CLEARING_EXCEL', 5, '微信订单号', 'channel_txn_id', 'string', NULL, NULL, NULL, 1),
    ('WX_CLEARING_EXCEL', 12, '应结订单金额', 'amount', 'decimal', NULL, '元', NULL, 1),
    ('WX_CLEARING_EXCEL', 22, '手续费', 'fee', 'decimal', NULL, '元', NULL, 0),
    ('WX_CLEARING_EXCEL', 9, '交易状态', 'status', 'enum', NULL, NULL, '{\"支付成功\":\"SUCCESS\",\"退款\":\"REFUND\"}', 1),
    ('WX_CLEARING_EXCEL', 8, '交易类型', 'biz_type', 'string', NULL, NULL, NULL, 0);


INSERT INTO task_config (
    task_id, name, channel_id, data_type, fetch_mode, data_form, fetch_config, parser_rule_id
) VALUES (
             'WX_CLEARING_TASK',
             '微信清算文件上传任务',
             'WX',                       -- 渠道标识
             'CLEARING',                 -- 数据类型
             'UPLOAD',                   -- 上传模式
             'FILE',                     -- 数据形式：文件
             '{"allowedExt":"xlsx"}',    -- fetch_config 可放一些约束或提示
             'WX_CLEARING_EXCEL'         -- 对应上一步的 parser_rule
         );


ALTER TABLE parser_rule  ADD COLUMN skip_tail_rows INT DEFAULT 0 COMMENT '需要跳过的尾部行数';

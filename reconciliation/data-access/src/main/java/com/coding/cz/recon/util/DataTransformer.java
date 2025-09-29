package com.coding.cz.recon.util;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2025-09-28
 */

import com.coding.cz.recon.entity.ParserRuleDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

public class DataTransformer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 统一转换入口
     */
    public static Object transform(String raw, ParserRuleDetail d) {
        if (raw == null || raw.trim().isEmpty()) {
            return d.getDefaultValue();
        }

        raw = cleanCellValue(raw);

        switch (d.getDataType().toLowerCase()) {
            case "string":
                return raw;

            case "date":
            case "datetime":
                return parseDateTime(raw, d.getFormatExpr());

            case "decimal":
                return parseDecimal(raw, d.getUnit(), d.getFormatExpr());

            case "enum":
                return mapEnum(raw, d.getEnumMapping());

            default:
                return raw;
        }
    }

    /**
     * 去掉 Excel 单元格可能的反引号/单引号
     */
    private static String cleanCellValue(String raw) {
        return raw.trim().replaceAll("^[`']+", "");
    }

    /**
     * 日期时间解析，支持 format_expr
     * 支持 Excel 数字日期
     */
    private static LocalDateTime parseDateTime(String raw, String format) {
        if (format != null) {
            try {
                return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException ex) {
                // fallback: Excel 数字日期
                if (raw.matches("^\\d+(\\.\\d+)?$")) {
                    Date javaDate = DateUtil.getJavaDate(Double.parseDouble(raw));
                    return javaDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                }
                throw ex;
            }
        } else {
            throw new IllegalArgumentException("date/datetime 字段缺少 format_expr");
        }
    }

    /**
     * decimal 解析，支持：
     * 1. 去掉千分位
     * 2. 单位转换（元 -> 分）
     * 3. format_expr 保留小数位数
     */
    private static BigDecimal parseDecimal(String raw, String unit, String formatExpr) {
        String clean = raw.trim();

        // 去掉千分位分隔符
        if (formatExpr != null && formatExpr.contains(",")) {
            clean = clean.replace(",", "");
        }

        BigDecimal val = new BigDecimal(clean);

        // 单位转换
        if ("元".equals(unit)) {
            val = val.multiply(BigDecimal.valueOf(100)); // 元 → 分
        }

        // 格式化小数位数
        if (formatExpr != null && formatExpr.matches("0+(\\.0+)?")) {
            int scale = formatExpr.contains(".")
                    ? formatExpr.substring(formatExpr.indexOf('.') + 1).length()
                    : 0;
            val = val.setScale(scale, RoundingMode.HALF_UP);
        }

        return val;
    }

    /**
     * enum 映射
     */
    private static String mapEnum(String raw, String enumMappingJson) {
        if (enumMappingJson == null) return raw;
        try {
            Map<String, String> mapping = objectMapper.readValue(enumMappingJson, new TypeReference<>() {});
            return mapping.getOrDefault(raw, raw);
        } catch (Exception e) {
            throw new RuntimeException("解析枚举映射失败: " + enumMappingJson, e);
        }
    }
}


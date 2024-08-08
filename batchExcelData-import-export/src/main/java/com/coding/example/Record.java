package com.coding.example;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-07-30
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Record {
    @ExcelProperty("id")
    private Long id;
    @ExcelProperty("value")
    private String value;

}

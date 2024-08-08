package com.coding.example;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EasyExcelExample {

    private static final int RECORDS_PER_SHEET = 200000;
    private static final int RECORDS_PER_WRITE = 10000;

    public static void main(String[] args) throws IOException {
        // 生成数据
        List<Record> data = generateData(1000000);
        // 创建 Excel 文件
        String fileName = "large_data.xlsx";

        log.info("==开始==");
        long s1 = System.currentTimeMillis();

        ExcelWriter excelWriter = null;
        try {
            // Initialize ExcelWriter
            excelWriter = EasyExcel.write(fileName, Record.class).build();
            //查询数据库获得记录总数 TODO
            int totalSheets = (data.size() + RECORDS_PER_SHEET - 1) / RECORDS_PER_SHEET;
            for (int sheetIndex = 0; sheetIndex < totalSheets; sheetIndex++) {
                log.info("sheet:{} start", sheetIndex + 1);

                long s2 = System.currentTimeMillis();
                int startRecord = sheetIndex * RECORDS_PER_SHEET;
                int endRecord = Math.min(startRecord + RECORDS_PER_SHEET, data.size());
                List<Record> sheetData = data.subList(startRecord, endRecord);

                // 创建新的 sheet
                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet" + (sheetIndex + 1)).build();

                // 分割数据并写入到当前 sheet
                for (int chunkStart = 0; chunkStart < sheetData.size(); chunkStart += RECORDS_PER_WRITE) {
                    long c1 = System.currentTimeMillis();
                    int chunkEnd = Math.min(chunkStart + RECORDS_PER_WRITE, sheetData.size());
                    //分页查询 TODO
                    List<Record> chunkData = sheetData.subList(chunkStart, chunkEnd);
                    excelWriter.write(chunkData, writeSheet);
                    long c2 = System.currentTimeMillis();
                    log.info("sheet :{} chunkStart {} chunkEnd {} {} ms", sheetIndex + 1, chunkStart, chunkEnd, c2 - c1);
                }
                long e2 = System.currentTimeMillis();
                log.info("sheet :{} end {} ms", sheetIndex + 1, e2 - s2);
            }
        } finally {
            // Ensure the writer is closed
            if (excelWriter != null) {
                excelWriter.finish();
            }
            long e1 = System.currentTimeMillis();
            log.info("==结束 {}ms {}s ==", e1 - s1, (e1 - s1) / 1000);

        }
    }

    private static List<Record> generateData(int numRecords) {
        List<Record> records = new ArrayList<>();
        for (long i = 0; i < numRecords; i++) {
            records.add(new Record(i, "value_" + i));
        }
        return records;
    }


}

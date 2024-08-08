package com.coding.example;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;

import java.util.ArrayList;
import java.util.List;

public class EasyExcelExample2{

    public static void main(String[] args) {
        // 生成测试数据
        List<List<String>> data = generateData(1000000);

        // 写入Excel
        writeExcel(data, "output.xlsx", 200000, 10000);
    }

    private static List<List<String>> generateData(int count) {
        List<List<String>> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<String> row = new ArrayList<>();
            row.add("Data " + (i + 1));
            data.add(row);
        }
        return data;
    }

    private static void writeExcel(List<List<String>> data, String fileName, int sheetSize, int batchSize) {
        ExcelWriterBuilder writerBuilder = EasyExcel.write(fileName);

        int totalDataSize = data.size();
        int sheetCount = (totalDataSize + sheetSize - 1) / sheetSize;

        for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
            int startRowIndex = sheetIndex * sheetSize;
            int endRowIndex = Math.min(startRowIndex + sheetSize, totalDataSize);

            // 创建一个新的Sheet
            ExcelWriterSheetBuilder sheetBuilder = writerBuilder.sheet("Sheet" + (sheetIndex + 1));

            for (int batchStartIndex = startRowIndex; batchStartIndex < endRowIndex; batchStartIndex += batchSize) {
                int batchEndIndex = Math.min(batchStartIndex + batchSize, endRowIndex);

                // 只写入当前批次的数据
                sheetBuilder.doWrite(data.subList(batchStartIndex, batchEndIndex));
            }
        }
    }
}

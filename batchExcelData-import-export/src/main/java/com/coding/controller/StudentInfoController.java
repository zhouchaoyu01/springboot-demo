package com.coding.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;

import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSONObject;

import com.coding.entity.StudentInfo;
import com.coding.example.Record;
import com.coding.service.StudentInfoService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Controller
public class StudentInfoController {

    @Autowired
    private StudentInfoService service;

    @GetMapping("/stu")
    public String list() {
//        List<StudentInfo> list = service.gList();
//        String jsonString = JSONObject.toJSONString(list);
//        System.out.println(jsonString);
        long s1 = System.currentTimeMillis();
        List<StudentInfo> stuList = service.findByPage(1, 20000);
        long s2 = System.currentTimeMillis();
        log.error("{} ms", s2 - s1);
        return "success";

    }

    @GetMapping("/import")
    public String importExcel(@RequestParam MultipartFile file) {


        return "success";
    }


    /**
     * 写100w数据到excel中   导出所用时间:41秒
     *
     * 写10个sheet 每个10w 每次写2w
     * @param response
     * @return
     */
    @GetMapping("/export")
    public String exportExcel(HttpServletResponse response) {
        {
            OutputStream outputStream = null;
            try {
                long startTime = System.currentTimeMillis();
                System.out.println("导出开始时间:" + startTime);

                outputStream = response.getOutputStream();
                String fileName = new String(("excel100w.xlsx").getBytes(), "UTF-8") ;
                // 创建ExcelWriter
//                ExcelWriter excelWriter = EasyExcel.write(fileName, StudentInfo.class).build();//本地文件
                ExcelWriter excelWriter = EasyExcel.write(outputStream, StudentInfo.class).build();//浏览器
                //模拟统计查询的数据数量这里模拟100w
                //记录总数:实际中需要根据查询条件进行统计即可
                Integer totalCount = Math.toIntExact(service.findCount());
                //每一个Sheet存放10w条数据
//                Integer sheetDataRows = ExcelConstants.PER_SHEET_ROW_COUNT;
                Integer sheetDataRows = 100000;
                //每次写入的数据量2w
//                Integer writeDataRows = ExcelConstants.PER_WRITE_ROW_COUNT;
                Integer writeDataRows = 20000;
                //计算需要的Sheet数量
                Integer sheetNum = totalCount % sheetDataRows == 0 ? (totalCount / sheetDataRows) : (totalCount / sheetDataRows + 1);
                //计算一般情况下每一个Sheet需要写入的次数(一般情况不包含最后一个sheet,因为最后一个sheet不确定会写入多少条数据)
                Integer oneSheetWriteCount = sheetDataRows / writeDataRows;
                //计算最后一个sheet需要写入的次数
                Integer lastSheetWriteCount = totalCount % sheetDataRows == 0 ? oneSheetWriteCount : (totalCount % sheetDataRows % writeDataRows == 0 ? (totalCount / sheetDataRows / writeDataRows) : (totalCount / sheetDataRows / writeDataRows + 1));

                //开始分批查询分次写入
                //注意这次的循环就需要进行嵌套循环了,外层循环是Sheet数目,内层循环是写入次数
                for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++) {
                    long s1 = System.currentTimeMillis();
                    log.info("sheet:{} start", sheetIndex + 1);
                    // 创建新的 sheet
                    WriteSheet writeSheet = EasyExcel.writerSheet("Sheet" + (sheetIndex + 1)).build();


                    //循环写入次数: j的自增条件是当不是最后一个Sheet的时候写入次数为正常的每个Sheet写入的次数,如果是最后一个就需要使用计算的次数lastSheetWriteCount
                    for (int j = 0; j < (sheetIndex != sheetNum - 1 ? oneSheetWriteCount : lastSheetWriteCount); j++) {
                        //分页查询一次2w
                        List<StudentInfo> stuList = service.findByPage(j + 1 + oneSheetWriteCount * sheetIndex, writeDataRows);
                        if (!CollectionUtils.isEmpty(stuList)) {
                            //写数据
                            excelWriter.write(stuList, writeSheet);
                        }

                    }
                    long s2 = System.currentTimeMillis();
                    log.info("sheet:{} end {} ms", sheetIndex + 1, s2-s1);
                }

                // 下载EXCEL
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName );
                excelWriter.finish();
                outputStream.flush();
                //导出时间结束
                long endTime = System.currentTimeMillis();
                System.out.println("导出结束时间:" + endTime + "ms");
                System.out.println("导出所用时间:" + (endTime - startTime) / 1000 + "秒");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return "success";
    }


}

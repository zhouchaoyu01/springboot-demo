package com.coding.entity.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.coding.entity.StudentInfo;
import com.coding.service.StudentInfoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-08
 */
// 事件监听
public class EasyExceGeneralDatalListener extends AnalysisEventListener<StudentInfo> {
    /**
     * 处理业务逻辑的Service,也可以是Mapper
     */
    private StudentInfoService service;

    /**
     * 用于存储读取的数据
     */
    private List<StudentInfo> dataList = new ArrayList<StudentInfo>();

    public EasyExceGeneralDatalListener() {
    }

    public EasyExceGeneralDatalListener(StudentInfoService service) {
        this.service = service;
    }

    /**
     * easyexcel invoke 报错 class java.util.LinkedHashMap cannot be cast to class com.coding.entity.StudentInfo (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; com.coding.entity.StudentInfo is in unnamed module of loader 'app')

      EasyExcel.read(fileName, StudentInfo.class, listener).sheet().doRead();
     */
    @Override
    public void invoke(StudentInfo data, AnalysisContext context) {
        //数据add进入集合
        dataList.add(data);
        //size是否为100000条:这里其实就是分批.当数据等于10w的时候执行一次插入
        if (dataList.size() >= 100000) {
            //存入数据库:数据小于1w条使用Mybatis的批量插入即可;
            saveData();
            //清理集合便于GC回收
            dataList.clear();
        }
    }

    /**
     * 保存数据到DB
     *
     * @param
     * @MethodName: saveData
     * @return: void
     */
    private void saveData() {
        service.import2DBFromExcel10w(dataList);
        dataList.clear();
    }

    /**
     * Excel中所有数据解析完毕会调用此方法
     *
     * @param: context
     * @MethodName: doAfterAllAnalysed
     * @return: void
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        dataList.clear();
    }
}


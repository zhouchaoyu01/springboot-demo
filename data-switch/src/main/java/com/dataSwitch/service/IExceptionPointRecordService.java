package com.dataSwitch.service;


import com.dataSwitch.base.bean.ExceptionPointRecord;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2024-09-03
 */
public interface IExceptionPointRecordService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param rowId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ExceptionPointRecord selectExceptionPointRecordByRowId(Long rowId);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param exceptionPointRecord 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ExceptionPointRecord> selectExceptionPointRecordList(ExceptionPointRecord exceptionPointRecord);

    /**
     * 新增【请填写功能名称】
     * 
     * @param exceptionPointRecord 【请填写功能名称】
     * @return 结果
     */
    public int insertExceptionPointRecord(ExceptionPointRecord exceptionPointRecord);

    /**
     * 修改【请填写功能名称】
     * 
     * @param exceptionPointRecord 【请填写功能名称】
     * @return 结果
     */
    public int updateExceptionPointRecord(ExceptionPointRecord exceptionPointRecord);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param rowIds 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteExceptionPointRecordByRowIds(Long[] rowIds);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param rowId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteExceptionPointRecordByRowId(Long rowId);
}

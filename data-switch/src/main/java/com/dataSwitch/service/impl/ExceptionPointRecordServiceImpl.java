package com.dataSwitch.service.impl;


import com.dataSwitch.base.bean.ExceptionPointRecord;
import com.dataSwitch.base.dao.ExceptionPointRecordMapper;
import com.dataSwitch.service.IExceptionPointRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-03
 */
@Service
public class ExceptionPointRecordServiceImpl implements IExceptionPointRecordService
{
    @Resource
    private ExceptionPointRecordMapper exceptionPointRecordMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param rowId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ExceptionPointRecord selectExceptionPointRecordByRowId(Long rowId)
    {
        return exceptionPointRecordMapper.selectExceptionPointRecordByRowId(rowId);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param exceptionPointRecord 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ExceptionPointRecord> selectExceptionPointRecordList(ExceptionPointRecord exceptionPointRecord)
    {
        return exceptionPointRecordMapper.selectExceptionPointRecordList(exceptionPointRecord);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param exceptionPointRecord 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertExceptionPointRecord(ExceptionPointRecord exceptionPointRecord)
    {
        exceptionPointRecord.setCreateTime(new Date());
        return exceptionPointRecordMapper.insertExceptionPointRecord(exceptionPointRecord);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param exceptionPointRecord 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateExceptionPointRecord(ExceptionPointRecord exceptionPointRecord)
    {
        return exceptionPointRecordMapper.updateExceptionPointRecord(exceptionPointRecord);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param rowIds 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteExceptionPointRecordByRowIds(Long[] rowIds)
    {
        return exceptionPointRecordMapper.deleteExceptionPointRecordByRowIds(rowIds);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param rowId 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteExceptionPointRecordByRowId(Long rowId)
    {
        return exceptionPointRecordMapper.deleteExceptionPointRecordByRowId(rowId);
    }
}

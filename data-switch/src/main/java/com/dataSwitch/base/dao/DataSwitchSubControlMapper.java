package com.dataSwitch.base.dao;


import com.dataSwitch.base.bean.DataSwitchSubControl;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-08-29
 */
public interface DataSwitchSubControlMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param rowId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public DataSwitchSubControl selectDataSwitchSubControlByRowId(Long rowId);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param dataSwitchSubControl 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<DataSwitchSubControl> selectDataSwitchSubControlList(DataSwitchSubControl dataSwitchSubControl);

    /**
     * 新增【请填写功能名称】
     * 
     * @param dataSwitchSubControl 【请填写功能名称】
     * @return 结果
     */
    public int insertDataSwitchSubControl(DataSwitchSubControl dataSwitchSubControl);

    /**
     * 修改【请填写功能名称】
     * 
     * @param dataSwitchSubControl 【请填写功能名称】
     * @return 结果
     */
    public int updateDataSwitchSubControl(DataSwitchSubControl dataSwitchSubControl);

    /**
     * 删除【请填写功能名称】
     * 
     * @param rowId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataSwitchSubControlByRowId(Long rowId);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param rowIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataSwitchSubControlByRowIds(Long[] rowIds);
}

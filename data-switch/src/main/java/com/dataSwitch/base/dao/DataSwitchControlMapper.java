package com.dataSwitch.base.dao;


import com.dataSwitch.base.bean.DataSwitchControl;

import java.util.List;

/**
 * bMapper接口
 * 
 * @author zcy
 * @date 2024-08-29
 */
public interface DataSwitchControlMapper 
{
    /**
     * 查询b
     * 
     * @param rowId b主键
     * @return b
     */
    public DataSwitchControl selectDataSwitchControlByRowId(Long rowId);

    /**
     * 查询b列表
     * 
     * @param dataSwitchControl b
     * @return b集合
     */
    public List<DataSwitchControl> selectDataSwitchControlList(DataSwitchControl dataSwitchControl);

    /**
     * 新增b
     * 
     * @param dataSwitchControl b
     * @return 结果
     */
    public int insertDataSwitchControl(DataSwitchControl dataSwitchControl);

    /**
     * 修改b
     * 
     * @param dataSwitchControl b
     * @return 结果
     */
    public int updateDataSwitchControl(DataSwitchControl dataSwitchControl);

    /**
     * 删除b
     * 
     * @param rowId b主键
     * @return 结果
     */
    public int deleteDataSwitchControlByRowId(Long rowId);

    /**
     * 批量删除b
     * 
     * @param rowIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataSwitchControlByRowIds(Long[] rowIds);
}

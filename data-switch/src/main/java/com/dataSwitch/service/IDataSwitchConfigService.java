package com.dataSwitch.service;

import com.dataSwitch.base.bean.DataSwitchControl;
import com.dataSwitch.base.bean.DataSwitchControlExt;
import com.dataSwitch.base.bean.DataSwitchSubControl;
import com.dataSwitch.base.bean.DatabaseConfig;

import java.util.List;

/**
 * Created by sunlei on 2020/11/17.
 */
public interface IDataSwitchConfigService {

//    DataSwitchControl getDataSwitchControlById(Long id);
//
//    List<DataSwitchControlExt> getDataSwitchControlByIdAndPriority(Long id);
//
//    void refreshDataSwitchControlByIdAndPriority(Long id);
//
//    void refreshDataSwitchControlById(Long id);
//
    List<DataSwitchControl> getAllDataSwitchControl();
//
    List<DataSwitchControlExt> getAllDataSwitchControlByPriority();
//
    DatabaseConfig getDatabaseConfigById(Long id);
//
//    void refreshAllDataSwitchControl();
//
//    void refreshDatabaseConfigById(Long id);
//
//    List<DatabaseConfig> getAllDatabaseConfig(String status);
//
//    List<DataSwitchSubControl> getDataSwitchSubControlByDsId(Long dsId,String sourceType);
//
//    void updateStartTime(Long id, String startTime);
//
    void updateSubControlStartTime(DataSwitchSubControl dataSwitchSubControl);
//
//    void saveRecord(ExceptionPointRecord record);
//
//    List<ExceptionPointRecord> getAllExcptionRecord(Date nowDate);
//
//    List<CompareSubTask> getSubTaskByMainId(Long mainId,String status);

//    int existSubTaskByMainId(Long mainId,String status);
//
//    int updateMainStatusById(Long rowId,String status);
//
//    int updateSubStatusById(Long rowId,String status);
//
//    int updateSubCountById(Long rowId,int count);

//    void createCompareSubTask(CompareSubTask compareSubTask);
//
//    void updateAllExcptionRecord(Date nowDate);
//
//    CompareMainTask getLastMainTask(Long dscId);
//
    List<DataSwitchSubControl> getDataSwitchSubControlByMainId(Long id);

    List<DataSwitchSubControl> getDataSwitchSubControlByMainIdAndPriority(Long id, String sourceType, String priority);

//    DataSwitchSubControl getDataSwitchSubControlById(Long id);
//
//    void refreshDataSwitchSubControlByMainId(Long id,String sourceType);
//
//    void refreshDataSwitchSubControlById(Long id);
//
//    void updateMainControlBySub();
//
//    int existsUncompleted(String startTime, String endTime, String tableName, String status);
}

package com.dataSwitch.service.impl;


import com.dataSwitch.base.bean.*;
import com.dataSwitch.base.dao.*;
import com.dataSwitch.service.IDataSwitchConfigService;
import com.dataSwitch.utils.DataSwitchConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by sunlei on 2020/11/17.
 */
@Service("dataSwitchConfigService")
public class DataSwitchConfigServiceImpl implements IDataSwitchConfigService {

//    @Autowired
//    private DataSwitchControlMapper dataSwitchControlMapper;
    @Resource
    private DatabaseConfigMapper databaseConfigMapper;
    @Resource
    private ExceptionPointRecordMapper exceptionPointRecordMapper;
//    @Autowired
//    private CompareSubTaskMapper compareSubTaskMapper;
//    @Autowired
//    private CompareMainTaskMapper compareMainTaskMapper;
    @Resource
    private DataSwitchControlMapper dataSwitchControlMapper;
    @Resource
    private DataSwitchSubControlMapper dataSwitchSubControlMapper;
    @Resource
    private DataSwitchControlExtMapper dataSwitchControlExtMapper;
//    @Autowired
//    private CompareMainTaskMapperExt compareMainTaskMapperExt;

    public static final String CACHE_DATASWITCH_CONTROL = "DATASWITCH_CONTROL";
    public static final String CACHE_DATASWITCH_SUB_CONTROL = "DATASWITCH_SUB_CONTROL";
    public static final String CACHE_DATASWITCH_DATABASE = "DATASWITCH_DATABASE";
//
//    @Cacheable(value = CACHE_DATASWITCH_CONTROL,key = "'getDataSwitchControlById_'+#id",unless = "#result == null")
//    public DataSwitchControl getDataSwitchControlById(Long id)
//    {
//        return dataSwitchControlMapper.selectByPrimaryKey(id);
//    }
//
//    @Cacheable(value = CACHE_DATASWITCH_CONTROL,key = "'getDataSwitchControlByIdAndPriority'+#id",unless = "#result == null")
//    public List<DataSwitchControlExt> getDataSwitchControlByIdAndPriority(Long id)
//    {
//        return dataSwitchControlExtMapper.selectAllByPriorityAndId(id);
//    }
//
//    @CacheEvict(value = CACHE_DATASWITCH_CONTROL,key = "'getDataSwitchControlByIdAndPriority'+#id")
//    public void refreshDataSwitchControlByIdAndPriority(Long id)
//    {
//        //刷新DataSwitchControl缓存
//    }
//
//    @CacheEvict(value = CACHE_DATASWITCH_CONTROL,key = "'getDataSwitchControlById_'+#id")
//    public void refreshDataSwitchControlById(Long id)
//    {
//        //刷新DataSwitchControl缓存
//    }
//
//    @Cacheable(value = CACHE_DATASWITCH_CONTROL,key = "'getAllDataSwitchControl_*'",unless = "#result == null")
    public List<DataSwitchControl> getAllDataSwitchControl()
    {
        DataSwitchControl dataSwitchControl = new DataSwitchControl();
        dataSwitchControl.setStatus(DataSwitchConstants.STATUS_0);//状态：0 开通
        return dataSwitchControlMapper.selectDataSwitchControlList(dataSwitchControl);
    }

    public List<DataSwitchControlExt> getAllDataSwitchControlByPriority()
    {
        return dataSwitchControlExtMapper.selectAllByPriority();
    }
//
//    @CacheEvict(value = CACHE_DATASWITCH_CONTROL,key = "'getAllDataSwitchControl_*'")
//    public void refreshAllDataSwitchControl()
//    {
//    }
//
//    @Cacheable(value = CACHE_DATASWITCH_DATABASE,key = "'getDatabaseConfigById_'+#id",unless = "#result == null")
    public DatabaseConfig getDatabaseConfigById(Long id)
    {
        return databaseConfigMapper.selectDatabaseConfigByDsId(id);
    }
//
//    @CacheEvict(value = CACHE_DATASWITCH_DATABASE,key = "'getDatabaseConfigById_'+#id")
//    public void refreshDatabaseConfigById(Long id)
//    {
//        //刷新Database缓存
//    }
//
//    //@Cacheable(value = CACHE_DATASWITCH_DATABASE,key = "'getDatabaseConfigById_'+#id",unless = "#result == null")
//    public List<DatabaseConfig> getAllDatabaseConfig(String status)
//    {
//        DatabaseConfigExample example = new DatabaseConfigExample();
//        if(StringUtils.isNotBlank(status))
//        {
//            example.createCriteria().andStatusEqualTo(status);
//        }
//        return databaseConfigMapper.selectByExample(example);
//    }
//
//    public List<DataSwitchSubControl> getDataSwitchSubControlByDsId(Long dsId,String sourceType) {
//        DataSwitchSubControlExample example = new DataSwitchSubControlExample();
//        DataSwitchSubControlExample.Criteria criteria = example.createCriteria();
//        if(null!=dsId)
//        {
//            criteria.andDsIdEqualTo(dsId);
//        }
//        criteria.andSourceTypeEqualTo(sourceType).andStatusEqualTo(DataSwitchConstants.STATUS_0);
//        return dataSwitchSubControlMapper.selectByExample(example);
//    }
//
//    /**
//     *
//     * @param id control转移任务主表主键
//     * @param sourceType 类型：0 src源库，1 des目标库
//     * @return
//     */
//    @Cacheable(value = CACHE_DATASWITCH_SUB_CONTROL,key = "'getDataSwitchSubControlByMainId_'+#id+'_'+#sourceType",unless = "#result == null")
    @Override
    public List<DataSwitchSubControl> getDataSwitchSubControlByMainId(Long id) {
        DataSwitchSubControl example = new DataSwitchSubControl();
        example.setMainId(id);
        return dataSwitchSubControlMapper.selectDataSwitchSubControlList(example);
    }
//
    public List<DataSwitchSubControl> getDataSwitchSubControlByMainIdAndPriority(Long id, String sourceType, String priority) {
        DataSwitchSubControl dataSwitchSubControl = new DataSwitchSubControl();
        dataSwitchSubControl.setMainId(id);
        dataSwitchSubControl.setPriorityLevel(priority);
        dataSwitchSubControl.setStatus("0");
        return dataSwitchSubControlMapper.selectDataSwitchSubControlList(dataSwitchSubControl);
    }
//
//    @Cacheable(value = CACHE_DATASWITCH_SUB_CONTROL,key = "'getDataSwitchSubControlById_'+#id",unless = "#result == null")
//    @Override
//    public DataSwitchSubControl getDataSwitchSubControlById(Long id) {
//        return dataSwitchSubControlMapper.selectByPrimaryKey(id);
//    }
//
//    @CacheEvict(value = CACHE_DATASWITCH_SUB_CONTROL,key = "'getDataSwitchSubControlByMainId_'+#id+'_'+#sourceType")
//    @Override
//    public void refreshDataSwitchSubControlByMainId(Long id,String sourceType) {
//    }
//
//    @CacheEvict(value = CACHE_DATASWITCH_SUB_CONTROL,key = "'getDataSwitchSubControlById_'+#id")
//    @Override
//    public void refreshDataSwitchSubControlById(Long id) {
//    }
//
//    public void updateStartTime(Long id, String startTime)
//    {
//        DataSwitchControl dataSwitchConfig = new DataSwitchControl();
//        dataSwitchConfig.setRowId(id);
//        dataSwitchConfig.setStartTime(startTime);
//        dataSwitchConfig.setLastModifyTime(new Date());
//        dataSwitchControlMapper.updateByPrimaryKeySelective(dataSwitchConfig);
//    }
//
    public void updateSubControlStartTime(DataSwitchSubControl dataSwitchSubControl)
    {
        dataSwitchSubControl.setUpdateTime(new Date());
        dataSwitchSubControlMapper.updateDataSwitchSubControl(dataSwitchSubControl);
    }
//
    public void saveRecord(ExceptionPointRecord record)
    {
        exceptionPointRecordMapper.insertExceptionPointRecord(record);
    }
//
//    public List<ExceptionPointRecord> getAllExcptionRecord(Date nowDate)
//    {
//        ExceptionPointRecordExample exceptionPointRecordExample = new ExceptionPointRecordExample();
//        ExceptionPointRecordExample.Criteria criteria = exceptionPointRecordExample.createCriteria();
//        if(null!=nowDate)
//        {
//            criteria.andCreateTimeLessThan(nowDate);
//        }
//        criteria.andStatusEqualTo(DataSwitchConstants.STATUS_0);
//        exceptionPointRecordExample.setOrderByClause("CREATE_TIME ASC");
//        return exceptionPointRecordMapper.selectByExample(exceptionPointRecordExample);
//    }
//
//    public List<CompareSubTask> getSubTaskByMainId(Long mainId,String status)
//    {
//        CompareSubTaskExample example = new CompareSubTaskExample();
//        CompareSubTaskExample.Criteria criteria = example.createCriteria();
//        criteria.andMainIdEqualTo(mainId);
//        if(StringUtils.isNotBlank(status))
//        {
//            criteria.andStatusEqualTo(status);
//        }
//        return compareSubTaskMapper.selectByExample(example);
//    }
//
//    public int existSubTaskByMainId(Long mainId,String status)
//    {
//        CompareSubTaskExample example = new CompareSubTaskExample();
//        CompareSubTaskExample.Criteria criteria = example.createCriteria();
//        criteria.andMainIdEqualTo(mainId);
//        if(StringUtils.isNotBlank(status))
//        {
//            criteria.andStatusEqualTo(status);
//        }
//        return compareSubTaskMapper.countByExample(example);
//    }
//
//    public int updateMainStatusById(Long rowId,String status)
//    {
//        CompareMainTask compareMainTask = new CompareMainTask();
//        compareMainTask.setRowId(rowId);
//        compareMainTask.setLastModifyTime(new Date());
//        compareMainTask.setStatus(status);
//        return compareMainTaskMapper.updateByPrimaryKeySelective(compareMainTask);
//    }
//
//    public int updateSubStatusById(Long rowId,String status)
//    {
//        CompareSubTask compareSubTask = new CompareSubTask();
//        compareSubTask.setRowId(rowId);
//        compareSubTask.setLastModifyTime(new Date());
//        compareSubTask.setStatus(status);
//        return compareSubTaskMapper.updateByPrimaryKeySelective(compareSubTask);
//    }
//
//    public int updateSubCountById(Long rowId,int count)
//    {
//        CompareSubTask compareSubTask = new CompareSubTask();
//        compareSubTask.setRowId(rowId);
//        compareSubTask.setLastModifyTime(new Date());
//        compareSubTask.setExcuCount(count+1);
//        return compareSubTaskMapper.updateByPrimaryKeySelective(compareSubTask);
//    }
//
//    public void createCompareSubTask(CompareSubTask compareSubTask)
//    {
//        compareSubTaskMapper.insertSelective(compareSubTask);
//    }
//
//    /**
//     * 更新指定时间内所有断点状态为已处理
//     * @param nowDate
//     * @return
//     */
//    public void updateAllExcptionRecord(Date nowDate)
//    {
//        ExceptionPointRecordExample exceptionPointRecordExample = new ExceptionPointRecordExample();
//        ExceptionPointRecordExample.Criteria criteria = exceptionPointRecordExample.createCriteria();
//        if(null!=nowDate)
//        {
//            criteria.andCreateTimeLessThan(nowDate);
//        }
//        criteria.andStatusEqualTo(DataSwitchConstants.STATUS_0);
//        ExceptionPointRecord record = new ExceptionPointRecord();
//        record.setStatus(DataSwitchConstants.STATUS_1);
//        record.setLastModifyTime(new Date());
//        exceptionPointRecordMapper.updateByExampleSelective(record,exceptionPointRecordExample);
//    }
//
//    /**
//     * 根据转移配置主键获取主任务表中最新的一条比对任务
//     * @param dscId
//     * @return
//     */
//    public CompareMainTask getLastMainTask(Long dscId)
//    {
//        CompareMainTaskExample compareMainTaskExample = new CompareMainTaskExample();
//        compareMainTaskExample.createCriteria().andDscIdEqualTo(dscId).andTaskTypeEqualTo(DataSwitchConstants.TASK_TYPE_1);
//        compareMainTaskExample.setOrderByClause("CREATE_TIME DESC");
//        List<CompareMainTask> compareMainTasks = compareMainTaskMapper.selectByExample(compareMainTaskExample);
//        if(CollectionUtils.isNotEmpty(compareMainTasks))
//        {
//            return compareMainTasks.get(0);
//        }
//        return null;
//    }
//
//    public void updateMainControlBySub()
//    {
//        dataSwitchControlExtMapper.updateMainControlBySub();
//    }
//
//    /**
//     * 统计在时间戳范围内是否存在转移未对平的数据
//     * @param startTime
//     * @param endTime
//     * @return
//     */
//    public int existsUncompleted(String startTime, String endTime, String tableName, String status)
//    {
//        return compareMainTaskMapperExt.existsUncompleted(startTime,endTime,tableName,status);
//    }
}

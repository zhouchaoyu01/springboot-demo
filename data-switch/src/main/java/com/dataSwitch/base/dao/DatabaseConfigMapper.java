package com.dataSwitch.base.dao;


import com.dataSwitch.base.bean.DatabaseConfig;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-08-29
 */
public interface DatabaseConfigMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param dsId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public DatabaseConfig selectDatabaseConfigByDsId(Long dsId);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param databaseConfig 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<DatabaseConfig> selectDatabaseConfigList(DatabaseConfig databaseConfig);

    /**
     * 新增【请填写功能名称】
     * 
     * @param databaseConfig 【请填写功能名称】
     * @return 结果
     */
    public int insertDatabaseConfig(DatabaseConfig databaseConfig);

    /**
     * 修改【请填写功能名称】
     * 
     * @param databaseConfig 【请填写功能名称】
     * @return 结果
     */
    public int updateDatabaseConfig(DatabaseConfig databaseConfig);

    /**
     * 删除【请填写功能名称】
     * 
     * @param dsId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDatabaseConfigByDsId(Long dsId);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param dsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDatabaseConfigByDsIds(Long[] dsIds);
}

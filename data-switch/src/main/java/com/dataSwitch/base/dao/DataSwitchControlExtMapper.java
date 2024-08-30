package com.dataSwitch.base.dao;


import com.dataSwitch.base.bean.DataSwitchControlExt;

import java.util.List;

/**
 * Created by sunlei on 2021/2/7.
 */
public interface DataSwitchControlExtMapper {

    /**
     * 主表关联子表，并区分子任务是否用不同线程执行
     * @return
     */
    List<DataSwitchControlExt> selectAllByPriority();

//    List<DataSwitchControlExt> selectAllByPriorityAndId(Long id);
//
//    void updateMainControlBySub();
}

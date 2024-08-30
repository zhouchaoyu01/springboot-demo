package com.dataSwitch.base.bean;

/**
 * Created by sunlei on 2021/2/7.
 */
public class DataSwitchControlExt extends DataSwitchControl {

    /**
     * 用于标识子任务是否要在一个线程内执行（计划同一个库的子任务在同线程，不同库的用不同线程执行）
     */
    private String priorityLevel;

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
}

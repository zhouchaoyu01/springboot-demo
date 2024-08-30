package com.dataSwitch.service;

/**
 * Created by sunlei on 2020/12/9.
 */
public interface IQueueProcessor {

    void process(Object message, String key);

    void singleProcess(Object message, String key);
}

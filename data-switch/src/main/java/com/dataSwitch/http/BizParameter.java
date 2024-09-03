package com.dataSwitch.http;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 业务参数
 */
public class BizParameter extends JSONObject {
    public void addParam(String paramName, String paramValue) {
        this.put(paramName, paramValue);
    }

    public void addParam(String paramName, int paramValue) {
        this.put(paramName, paramValue);
    }

    public void addParam(String paramName, Long paramValue) {
        this.put(paramName, paramValue);
    }

    public void addMapParam(String paramName, Map<String, Object> paramMap) {
        this.put(paramName, paramMap);
    }

    public void addListParam(String paramName, List paramValue) {
        this.put(paramName, paramValue);
    }
}

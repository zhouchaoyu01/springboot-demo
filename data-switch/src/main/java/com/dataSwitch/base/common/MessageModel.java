package com.dataSwitch.base.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by sunlei on 2021/1/5.
 */
public class MessageModel implements Serializable {

    private static final long serialVersionUID = 96585774520215003L;

    /**
     * DATA_SWITCH_CONTROL表主键ROW_ID
     */
    private String id;
    /**
     * DATA_SWITCH_SUB_CONTROL表主键ROW_ID
     */
    private Long subId;
    /**
     * 源表主键
     */
    private String orderId;

//    private String processingType;

    private String startTime;
    private String endTime;

    private String srcTbName;
    private String distTbName;
    /**
     * 数据值
     */
    private JSONObject jsonObject;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSubId() {
        return subId;
    }

    public void setSubId(Long subId) {
        this.subId = subId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSrcTbName() {
        return srcTbName;
    }

    public void setSrcTbName(String srcTbName) {
        this.srcTbName = srcTbName;
    }

    public String getDistTbName() {
        return distTbName;
    }

    public void setDistTbName(String distTbName) {
        this.distTbName = distTbName;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "id='" + id + '\'' +
                ", subId=" + subId +
                ", orderId='" + orderId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", srcTbName='" + srcTbName + '\'' +
                ", distTbName='" + distTbName + '\'' +
                ", jsonObject=" + jsonObject +
                '}';
    }
}

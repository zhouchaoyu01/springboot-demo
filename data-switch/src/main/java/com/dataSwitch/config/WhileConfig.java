package com.dataSwitch.config;

/**
 * 循环标识，单例
 *
 * Created by sunlei on 2020/12/22.
 */
public class WhileConfig {

    private boolean whileFlag = true;

    private String whileStr="";

    private WhileConfig(){}

    private static WhileConfig whileConfig=new WhileConfig();

    public static WhileConfig getInstance(){ return whileConfig;}

    public boolean isWhileFlag() {
        return whileFlag;
    }

    public void setWhileFlag(boolean whileFlag) {
        this.whileFlag = whileFlag;
    }

    public String getWhileStr() {
        return whileStr;
    }

    public void setWhileStr(String whileStr) {
        this.whileStr = whileStr;
    }
}

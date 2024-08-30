package com.dataSwitch.base.common;

/**
 * Created by sunlei on 2020/11/18.
 */
public class DataSwitchException extends Exception {


    private static final long serialVersionUID = -3125815613418447089L;

    private String errorCode;

    public DataSwitchException()
    {
        super();
    }

    public DataSwitchException(String businessCode)
    {
        this(businessCode, "");
    }

    public DataSwitchException(String code, String message)
    {
        super(message);
        errorCode = code;
    }

    public DataSwitchException(String message, Throwable t)
    {
        super(message, t);
    }

    public DataSwitchException(String code, String message, Throwable t)
    {
        super(message, t);
        errorCode = code;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

}

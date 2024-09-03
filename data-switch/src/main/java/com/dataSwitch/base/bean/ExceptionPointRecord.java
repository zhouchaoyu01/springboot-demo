package com.dataSwitch.base.bean;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 exception_point_record
 * 
 * @author ruoyi
 * @date 2024-09-03
 */
public class ExceptionPointRecord {
    private static final long serialVersionUID = 1L;

    private Long rowId;


    private Long dscId;


    private String exceptionTime;


    private String exceptionCause;


    private String status;

    private Date createTime;
    private Date lastModifyTime;

    public void setRowId(Long rowId) 
    {
        this.rowId = rowId;
    }

    public Long getRowId() 
    {
        return rowId;
    }
    public void setDscId(Long dscId) 
    {
        this.dscId = dscId;
    }

    public Long getDscId() 
    {
        return dscId;
    }
    public void setExceptionTime(String exceptionTime) 
    {
        this.exceptionTime = exceptionTime;
    }

    public String getExceptionTime() 
    {
        return exceptionTime;
    }
    public void setExceptionCause(String exceptionCause) 
    {
        this.exceptionCause = exceptionCause;
    }

    public String getExceptionCause() 
    {
        return exceptionCause;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setLastModifyTime(Date lastModifyTime) 
    {
        this.lastModifyTime = lastModifyTime;
    }

    public Date getLastModifyTime() 
    {
        return lastModifyTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("rowId", getRowId())
            .append("dscId", getDscId())
            .append("exceptionTime", getExceptionTime())
            .append("exceptionCause", getExceptionCause())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("lastModifyTime", getLastModifyTime())
            .toString();
    }
}

package com.coding.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 
 * @TableName student_info
 */
@Data
public class StudentInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    @ExcelProperty("id")
    private Integer id;

    /**
     * 
     */
    @ExcelProperty("学生id")
    private Integer studentId;

    /**
     * 
     */
    @ExcelProperty("学生姓名")
    private String name;

    /**
     * 
     */
    @ExcelProperty("课程id")
    private Integer courseId;

    /**
     * 
     */
    @ExcelProperty("班级id")
    private Integer classId;

    /**
     * 
     */
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd" ,timezone = "GMT+8")
    private Date createTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentInfo that = (StudentInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(studentId, that.studentId) && Objects.equals(name, that.name) && Objects.equals(courseId, that.courseId) && Objects.equals(classId, that.classId) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, name, courseId, classId, createTime);
    }

}
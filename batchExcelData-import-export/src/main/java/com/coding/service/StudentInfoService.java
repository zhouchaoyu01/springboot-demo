package com.coding.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.coding.entity.StudentInfo;

import java.util.List;

/**
* @author ThinkPad
* @description 针对表【student_info】的数据库操作Service
* @createDate 2024-07-30 19:30:15
*/
public interface StudentInfoService extends IService<StudentInfo> {

    List<StudentInfo> gList();

    List<StudentInfo> findByPage(int pageNum, int pageSize);
    long findCount();

}

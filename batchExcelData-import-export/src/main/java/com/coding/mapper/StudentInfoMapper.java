package com.coding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coding.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author ThinkPad
* @description 针对表【student_info】的数据库操作Mapper
* @createDate 2024-07-30 19:30:15
* @Entity com.coding.StudentInfo
*/

@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {

    List<StudentInfo> gList();
}





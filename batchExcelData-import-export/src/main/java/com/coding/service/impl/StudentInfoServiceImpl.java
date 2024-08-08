package com.coding.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coding.entity.StudentInfo;
import com.coding.mapper.StudentInfoMapper;
import com.coding.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author ThinkPad
* @description 针对表【student_info】的数据库操作Service实现
* @createDate 2024-07-30 19:30:15
*/
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo>
    implements StudentInfoService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Override
    public List<StudentInfo> gList() {
        return studentInfoMapper.gList();
    }

    @Override
    public List<StudentInfo> findByPage(int pageNum, int pageSize) {
        Page<StudentInfo> page = new Page<>(pageNum, pageSize);
        IPage<StudentInfo> studentInfoPage = studentInfoMapper.selectPage(page, null);
        return studentInfoPage.getRecords();
    }

    @Override
    public long findCount() {
        return studentInfoMapper.selectCount(null);
    }
}





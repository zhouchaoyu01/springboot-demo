package com.coding.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coding.entity.StudentInfo;
import com.coding.mapper.StudentInfoMapper;
import com.coding.service.StudentInfoService;
import com.coding.util.JDBCDruidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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

    @Override
    public void import2DBFromExcel10w(List<StudentInfo> dataList) {
        //结果集中数据为0时,结束方法.进行下一次调用
        if (dataList.size() == 0) {
            return ;
        }
        //JDBC分批插入+事务操作完成对10w数据的插入
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long startTime = System.currentTimeMillis();
            System.out.println(dataList.size() + "条,开始导入到数据库时间:" + startTime + "ms");
            conn = JDBCDruidUtils.getConnection();
            //控制事务:默认不提交
            conn.setAutoCommit(false);
            String sql = "insert into student_info (student_id,name,course_id,class_id,create_time) values(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            //循环结果集:这里循环不支持"烂布袋"表达式
            for (int i = 0; i < dataList.size(); i++) {
                StudentInfo item = dataList.get(i);
                ps.setInt(1, item.getStudentId());
                ps.setString(2, item.getName());
                ps.setInt(3, item.getCourseId());
                ps.setInt(4, item.getClassId());
                ps.setDate(5, new Date(System.currentTimeMillis()));

                //将一组参数添加到此 PreparedStatement 对象的批处理命令中。
                ps.addBatch();
            }
            //执行批处理
            ps.executeBatch();
            //手动提交事务
            conn.commit();
            long endTime = System.currentTimeMillis();
            System.out.println(dataList.size() + "条,结束导入到数据库时间:" + endTime + "ms");
            System.out.println(dataList.size() + "条,导入用时:" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关连接
            JDBCDruidUtils.close(conn, ps);
        }


    }
}





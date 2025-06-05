package com.zzyl.nursing.service.impl;

import java.util.List;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.StudentsMapper;
import com.zzyl.nursing.domain.Students;
import com.zzyl.nursing.service.IStudentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
/**
 * 学生Service业务层处理
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@Service
public class StudentsServiceImpl extends ServiceImpl<StudentsMapper,Students> implements IStudentsService
{
    @Autowired
    private StudentsMapper studentsMapper;

    /**
     * 查询学生
     * 
     * @param id 学生主键
     * @return 学生
     */
    @Override
    public Students selectStudentsById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询学生列表
     * 
     * @param students 学生
     * @return 学生
     */
    @Override
    public List<Students> selectStudentsList(Students students)
    {
        return studentsMapper.selectStudentsList(students);
    }

    /**
     * 新增学生
     * 
     * @param students 学生
     * @return 结果
     */
    @Override
    public int insertStudents(Students students)
    {
        return save(students)?1:0;
    }

    /**
     * 修改学生
     * 
     * @param students 学生
     * @return 结果
     */
    @Override
    public int updateStudents(Students students)
    {
        return updateById(students)?1:0;
    }

    /**
     * 批量删除学生
     * 
     * @param ids 需要删除的学生主键
     * @return 结果
     */
    @Override
    public int deleteStudentsByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除学生信息
     * 
     * @param id 学生主键
     * @return 结果
     */
    @Override
    public int deleteStudentsById(Long id)
    {
        return removeById(id)?1:0;
    }

}

package com.zzyl.nursing.mapper;

import java.util.List;
import com.zzyl.nursing.domain.Students;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@Mapper
public interface StudentsMapper extends BaseMapper<Students>
{
    /**
     * 查询学生
     * 
     * @param id 学生主键
     * @return 学生
     */
    public Students selectStudentsById(Long id);

    /**
     * 查询学生列表
     * 
     * @param students 学生
     * @return 学生集合
     */
    public List<Students> selectStudentsList(Students students);

    /**
     * 新增学生
     * 
     * @param students 学生
     * @return 结果
     */
    public int insertStudents(Students students);

    /**
     * 修改学生
     * 
     * @param students 学生
     * @return 结果
     */
    public int updateStudents(Students students);

    /**
     * 删除学生
     * 
     * @param id 学生主键
     * @return 结果
     */
    public int deleteStudentsById(Long id);

    /**
     * 批量删除学生
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudentsByIds(Long[] ids);
}

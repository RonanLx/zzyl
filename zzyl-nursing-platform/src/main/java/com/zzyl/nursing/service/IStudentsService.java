package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.Students;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 学生Service接口
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
public interface IStudentsService extends IService<Students>
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
     * 批量删除学生
     * 
     * @param ids 需要删除的学生主键集合
     * @return 结果
     */
    public int deleteStudentsByIds(Long[] ids);

    /**
     * 删除学生信息
     * 
     * @param id 学生主键
     * @return 结果
     */
    public int deleteStudentsById(Long id);
}

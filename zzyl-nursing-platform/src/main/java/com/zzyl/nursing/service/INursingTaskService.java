package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.domain.NursingTask;
import com.zzyl.nursing.dto.NursingTaskDto;
import com.zzyl.nursing.dto.TaskDto;
import com.zzyl.nursing.vo.NursingTaskVo;

/**
 * 护理任务Service接口
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
public interface INursingTaskService extends IService<NursingTask>
{
    /**
     * 查询护理任务
     * 
     * @param id 护理任务主键
     * @return 护理任务
     */
    public NursingTaskVo selectNursingTaskById(Long id);

    /**
     * 查询护理任务列表
     * 
     * @param nursingTaskDto 护理任务
     * @return 护理任务集合
     */
    public TableDataInfo selectNursingTaskList(NursingTaskDto nursingTaskDto);

    /**
     * 新增护理任务
     * 
     * @param nursingTask 护理任务
     * @return 结果
     */
    public int insertNursingTask(NursingTask nursingTask);

    /**
     * 修改护理任务
     * 
     * @param nursingTask 护理任务
     * @return 结果
     */
    public int updateNursingTask(NursingTask nursingTask);

    /**
     * 批量删除护理任务
     * 
     * @param ids 需要删除的护理任务主键集合
     * @return 结果
     */
    public int deleteNursingTaskByIds(Long[] ids);

    /**
     * 删除护理任务信息
     * 
     * @param id 护理任务主键
     * @return 结果
     */
    public int deleteNursingTaskById(Long id);

    /**
     * 生成护理任务
     * @param elder
     */
    void createMonthTask(Elder elder);

    /**
     * 取消任务
     * @param taskDto
     * @return
     */
    int cancelTask(TaskDto taskDto);

    /**
     * 任务改期
     * @param taskDto
     * @return
     */
    int rescheduleTask(TaskDto taskDto);

    /**
     * 执行任务
     * @param taskDto
     * @return
     */
    int executeTask(TaskDto taskDto);
}

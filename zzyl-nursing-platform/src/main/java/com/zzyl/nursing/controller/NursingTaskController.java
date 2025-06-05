package com.zzyl.nursing.controller;

import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.NursingTask;
import com.zzyl.nursing.dto.NursingTaskDto;
import com.zzyl.nursing.dto.TaskDto;
import com.zzyl.nursing.service.INursingTaskService;
import com.zzyl.nursing.vo.NursingTaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 护理任务Controller
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@RestController
@RequestMapping("/nursing/nursingTask")
@Api(tags = "护理任务的接口")
public class NursingTaskController extends BaseController
{
    @Autowired
    private INursingTaskService nursingTaskService;

    /**
     * 查询护理任务列表
     */
    @ApiOperation("查询护理任务列表")
    @PreAuthorize("@ss.hasPermi('nursing:nursingTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(NursingTaskDto nursingTaskDto)
    {
        TableDataInfo tableDataInfo = nursingTaskService.selectNursingTaskList(nursingTaskDto);
        return tableDataInfo;
    }

    /**
     * 获取护理任务详细信息
     */
    @ApiOperation("获取护理任务详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:nursingTask:query')")
    @GetMapping(value = "/{id}")
    public R<NursingTaskVo> getInfo(@ApiParam(value = "护理任务ID", required = true)
            @PathVariable("id") Long id)
    {
        NursingTaskVo nursingTaskVo = nursingTaskService.selectNursingTaskById(id);

        return R.ok(nursingTaskVo);
    }

    @PutMapping("cancel")
    @ApiOperation("取消任务")
    @ApiImplicitParam(name = "taskDto",value = "护理任务DTO对象",required = true,dataType = "TaskDto")
    public AjaxResult cancelTask(@RequestBody TaskDto taskDto) {
        return toAjax(nursingTaskService.cancelTask(taskDto));
    }

    @PutMapping("/updateTime")
    @ApiOperation("任务改期")
    @ApiImplicitParam(name = "taskDto",value = "护理任务DTO对象",required = true,dataType = "TaskDto")
    public AjaxResult rescheduleTask(@RequestBody TaskDto taskDto) {
        return toAjax(nursingTaskService.rescheduleTask(taskDto));
    }

    @PutMapping("/do")
    @ApiOperation("任务执行")
    @ApiImplicitParam(name = "taskDto",value = "护理任务DTO对象",required = true,dataType = "TaskDto")
    public AjaxResult executeTask(@RequestBody TaskDto taskDto) {
        return toAjax(nursingTaskService.executeTask(taskDto));
    }

    /**
     * 新增护理任务
     */
    @ApiOperation("新增护理任务")
    @PreAuthorize("@ss.hasPermi('nursing:nursingTask:add')")
    @Log(title = "护理任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@ApiParam(value = "护理任务实体")
            @RequestBody NursingTask nursingTask)
    {
        return toAjax(nursingTaskService.insertNursingTask(nursingTask));
    }

    /**
     * 修改护理任务
     */
    @ApiOperation("修改护理任务")
    @PreAuthorize("@ss.hasPermi('nursing:nursingTask:edit')")
    @Log(title = "护理任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@ApiParam(value = "护理任务实体")
            @RequestBody NursingTask nursingTask)
    {
        return toAjax(nursingTaskService.updateNursingTask(nursingTask));
    }

    /**
     * 删除护理任务
     */
    @ApiOperation("删除护理任务")
    @PreAuthorize("@ss.hasPermi('nursing:nursingTask:remove')")
    @Log(title = "护理任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(nursingTaskService.deleteNursingTaskByIds(ids));
    }
}

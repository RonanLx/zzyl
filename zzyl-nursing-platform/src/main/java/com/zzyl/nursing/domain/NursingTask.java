package com.zzyl.nursing.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 护理任务对象 nursing_task
 * 
 * @author ruoyi
 * @date 2024-09-27
 */
@Data
public class NursingTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 护理员id 多个护理员以逗号分隔 */
    @Excel(name = "护理员id")
    private String nursingId;

    /** 项目id */
    @Excel(name = "项目id")
    private Long projectId;

    /** 护理项目名称 */
    @Excel(name = "护理项目名称")
    private String projectName;

    /** 老人id */
    @Excel(name = "老人id")
    private Long elderId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 床位编号 */
    @Excel(name = "床位编号")
    private String bedNumber;

    /** 预计服务时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预计服务时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime estimatedServerTime;

    /** 实际服务时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际服务时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime realServerTime;

    /** 执行记录 */
    @Excel(name = "执行记录")
    private String mark;

    /** 取消原因 */
    @Excel(name = "取消原因")
    private String cancelReason;

    /** 状态  1待执行 2已执行 3已关闭  */
    @Excel(name = "状态  1待执行 2已执行 3已关闭 ")
    private Integer status;

    /** 执行图片 */
    @Excel(name = "执行图片")
    private String taskImage;

    @TableField(exist = false)
    @ApiModelProperty(value = "护理人列表")
    private List<String> nursingName;



}

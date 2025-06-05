package com.zzyl.nursing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("护理任务DTO")
public class TaskDto {

    @ApiModelProperty(value = "护理任务ID",required = true)
    private Long taskId;

    @ApiModelProperty(value = "取消理由",required = true)
    private String reason;

    @ApiModelProperty(value = "执行时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime estimatedServerTime;

    @ApiModelProperty(value = "任务图片",required = true)
    private String taskImage;

    @ApiModelProperty(value = "执行记录",required = true)
    private String mark;



}

package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 处理报警数据请求模型
 */
@Data
@ApiModel("处理报警数据请求模型")
public class AlertDataHandleDto {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 处理结果
     */
    @ApiModelProperty(value = "处理结果")
    private String processingResult;

    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processingTime;


}
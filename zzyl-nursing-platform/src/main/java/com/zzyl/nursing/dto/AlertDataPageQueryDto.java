package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 报警数据分页查询请求模型
 *
 * @author Ronan_JoJo
 * @create 2023/12/5 18:54
 **/
@Data
@ApiModel("报警数据分页查询请求模型")
public class AlertDataPageQueryDto {
    /**
     * 开始报警时间
     */
    @ApiModelProperty(value = "开始报警时间", required = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束报警时间
     */
    @ApiModelProperty(value = "结束报警时间", required = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 设备名称（精确搜索）
     */
    @ApiModelProperty(value = "设备名称（精确搜索）", required = false)
    private String deviceName;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "状态，0：待处理，1：已处理", required = false)
    private Integer status;

    /**
     * 报警数据id
     */
    @ApiModelProperty(value = "报警数据id", required = false)
    private Long id;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", example = "1", required = true)
    private Integer pageNum;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小", example = "10", required = true)
    private Integer pageSize;
}

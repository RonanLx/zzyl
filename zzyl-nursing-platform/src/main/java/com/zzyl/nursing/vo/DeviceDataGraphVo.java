package com.zzyl.nursing.vo;

import cn.hutool.core.date.LocalDateTimeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备数据曲线图响应模型
 *
 * @author Ronan_JoJo
 * @create 2023/12/11 18:15
 **/
@Data
@ApiModel("设备数据曲线图响应模型")
public class DeviceDataGraphVo {

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String dateTime;

    /**
     * 数据
     */
    @ApiModelProperty(value = "数据")
    private Double dataValue;

    /**
     * 构建按日统计数据示例
     *
     * @param startTime 开始时间
     * @return 数据列表
     */
    public static List<DeviceDataGraphVo> dayInstance(LocalDateTime startTime) {
        List<DeviceDataGraphVo> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime dataTime = startTime.plusHours(i);
            DeviceDataGraphVo deviceDataGraphVo = new DeviceDataGraphVo();
            deviceDataGraphVo.setDateTime(LocalDateTimeUtil.format(dataTime, "HH:00"));
            deviceDataGraphVo.setDataValue(0.0);
            list.add(deviceDataGraphVo);
        }
        return list;
    }

    /**
     * 构建按周统计数据示例
     *
     * @param startTime 开始时间
     * @return 数据列表
     */
    public static List<DeviceDataGraphVo> weekInstance(LocalDateTime startTime) {
        List<DeviceDataGraphVo> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime dataTime = startTime.plusDays(i);
            DeviceDataGraphVo deviceDataGraphVo = new DeviceDataGraphVo();
            deviceDataGraphVo.setDateTime(LocalDateTimeUtil.format(dataTime, "MM.dd"));
            deviceDataGraphVo.setDataValue(0.0);
            list.add(deviceDataGraphVo);
        }
        return list;
    }
}

package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.NursingTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("护理任务VO")
public class NursingTaskVo extends NursingTask {

    /**
     * 护理等级名称
     */
    @ApiModelProperty(value = "护理等级名称")
    private String nursingLevelName;
    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;
    /**
     * 护理名称
     */
    @ApiModelProperty(value = "护理名称")
    private List<String> nursingName;

    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "执行人")
    private String updater;

}

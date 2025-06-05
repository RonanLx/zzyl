package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户老人关联实体类
 */
@Data
@ApiModel(value = "MemberElderDto", description = "客户老人关联实体类")
public class MemberElderDto {

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "备注")
    private String remark;

}



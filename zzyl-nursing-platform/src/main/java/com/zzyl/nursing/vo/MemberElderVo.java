package com.zzyl.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户老人关联实体类
 */
@Data
@ApiModel(value = "MemberElderVo对象", description = "客户老人关联实体类")
public class MemberElderVo {

    @ApiModelProperty(value = "客户id")
    private String mid;

    @ApiModelProperty(value = "客户对老人的称谓")
    private String mremark;

    @ApiModelProperty(value = "老人ID")
    private String elderId;

    @ApiModelProperty(value = "老人姓名")
    private String name;

    @ApiModelProperty(value = "老人头像地址")
    private String image;

    @ApiModelProperty(value = "床位编号")
    private String bedNumber;

    @ApiModelProperty(value = "房间名称")
    private String typeName;

    @ApiModelProperty(value = "设备ID")
    private String iotId;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "产品Key")
    private String productKey;

}



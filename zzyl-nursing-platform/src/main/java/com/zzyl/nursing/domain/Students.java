package com.zzyl.nursing.domain;

import lombok.Data;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 学生对象 students
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@Data
public class Students extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键，自动递增 */
    private Long id;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 学生的性别：男、女、其他 */
    @Excel(name = "学生的性别：男、女、其他")
    private String gender;

    /** 家庭住址 */
    @Excel(name = "家庭住址")
    private String address;

    /** 电子邮件地址 */
    @Excel(name = "电子邮件地址")
    private String email;

    /** 学生头像 */
    @Excel(name = "学生头像")
    private String avatar;



}

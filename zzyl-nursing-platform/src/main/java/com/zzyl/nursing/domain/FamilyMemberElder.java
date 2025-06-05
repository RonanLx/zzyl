package com.zzyl.nursing.domain;

import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 客户老人关联对象 family_member_elder
 * 
 * @author ruoyi
 * @date 2024-05-19
 */
@Data
public class FamilyMemberElder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 家属id */
    @Excel(name = "家属id")
    private Long familyMemberId;

    /** 老人id */
    @Excel(name = "老人id")
    private Long elderId;

}

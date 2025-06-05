package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.domain.FamilyMemberElder;
import com.zzyl.nursing.dto.MemberElderDto;
import com.zzyl.nursing.vo.FamilyMemberElderVo;
import com.zzyl.nursing.vo.MemberElderVo;

import java.util.List;

/**
 * 客户老人关联Service接口
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
public interface IFamilyMemberElderService extends IService<FamilyMemberElder>
{
    /**
     * 查询客户老人关联
     * 
     * @param id 客户老人关联主键
     * @return 客户老人关联
     */
    public FamilyMemberElder selectFamilyMemberElderById(Long id);

    /**
     * 查询客户老人关联列表
     * 
     * @param familyMemberElder 客户老人关联
     * @return 客户老人关联集合
     */
    public List<FamilyMemberElder> selectFamilyMemberElderList(FamilyMemberElder familyMemberElder);

    /**
     * 新增客户老人关联
     * 
     * @param familyMemberElder 客户老人关联
     * @return 结果
     */
    public int insertFamilyMemberElder(FamilyMemberElder familyMemberElder);

    /**
     * 修改客户老人关联
     * 
     * @param familyMemberElder 客户老人关联
     * @return 结果
     */
    public int updateFamilyMemberElder(FamilyMemberElder familyMemberElder);

    /**
     * 批量删除客户老人关联
     * 
     * @param ids 需要删除的客户老人关联主键集合
     * @return 结果
     */
    public int deleteFamilyMemberElderByIds(Long[] ids);

    /**
     * 删除客户老人关联信息
     * 
     * @param id 客户老人关联主键
     * @return 结果
     */
    public int deleteFamilyMemberElderById(Long id);

    /**
     * 保存家属与老人的关系
     * @param memberElderDto
     * @return
     */
    int add(MemberElderDto memberElderDto);

    /**
     * 我的家人列表
     * @return
     */
    List<FamilyMemberElderVo> my();

    /**
     * 分页查询客户老人关联列表
     * @param userId
     * @return
     */
    List<MemberElderVo> listByPage(Long userId);

    /**
     * 解绑与老人的关系
     * @param id
     * @return
     */
    int deleteById(Long id);
}

package com.zzyl.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.domain.FamilyMemberElder;
import com.zzyl.nursing.dto.MemberElderDto;
import com.zzyl.nursing.mapper.ElderMapper;
import com.zzyl.nursing.mapper.FamilyMemberElderMapper;
import com.zzyl.nursing.service.IFamilyMemberElderService;
import com.zzyl.nursing.vo.FamilyMemberElderVo;
import com.zzyl.nursing.vo.MemberElderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 客户老人关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-19
 */
@Service
public class FamilyMemberElderServiceImpl extends ServiceImpl<FamilyMemberElderMapper, FamilyMemberElder> implements IFamilyMemberElderService
{
    @Autowired
    private FamilyMemberElderMapper familyMemberElderMapper;

    /**
     * 查询客户老人关联
     * 
     * @param id 客户老人关联主键
     * @return 客户老人关联
     */
    @Override
    public FamilyMemberElder selectFamilyMemberElderById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询客户老人关联列表
     * 
     * @param familyMemberElder 客户老人关联
     * @return 客户老人关联
     */
    @Override
    public List<FamilyMemberElder> selectFamilyMemberElderList(FamilyMemberElder familyMemberElder)
    {
        return familyMemberElderMapper.selectFamilyMemberElderList(familyMemberElder);
    }

    /**
     * 新增客户老人关联
     * 
     * @param familyMemberElder 客户老人关联
     * @return 结果
     */
    @Override
    public int insertFamilyMemberElder(FamilyMemberElder familyMemberElder)
    {
        return save(familyMemberElder) ? 1 : 0;
    }

    /**
     * 修改客户老人关联
     * 
     * @param familyMemberElder 客户老人关联
     * @return 结果
     */
    @Override
    public int updateFamilyMemberElder(FamilyMemberElder familyMemberElder)
    {
        return updateById(familyMemberElder) ? 1 : 0;
    }

    /**
     * 批量删除客户老人关联
     * 
     * @param ids 需要删除的客户老人关联主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberElderByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除客户老人关联信息
     * 
     * @param id 客户老人关联主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberElderById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    @Autowired
    private ElderMapper elderMapper;

    /**
     * 保存家属与老人的关系
     * @param memberElderDto
     * @return
     */
    @Override
    public int add(MemberElderDto memberElderDto) {

        //根据身份证号查询老人
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Elder::getIdCardNo,memberElderDto.getIdCard());
        Elder elder = elderMapper.selectOne(lambdaQueryWrapper);

        //判断老人是否存在
        if(ObjectUtil.isEmpty(elder)){
            throw new BaseException("该老人未入住，请检查输入信息");
        }

        //当前登录人
        Long userId = UserThreadLocal.getUserId();

        //判断是否已经绑定
        long count = count(Wrappers.<FamilyMemberElder>lambdaQuery()
                .eq(FamilyMemberElder::getElderId, elder.getId())
                .eq(FamilyMemberElder::getFamilyMemberId, userId));
        if(count > 0){
            throw new BaseException("该老人已绑定，请勿重复绑定");
        }


        //保存家属与老人的关系
        FamilyMemberElder familyMemberElder = BeanUtil.toBean(memberElderDto, FamilyMemberElder.class);
        familyMemberElder.setElderId(elder.getId());
        familyMemberElder.setFamilyMemberId(UserThreadLocal.getUserId());
        return save(familyMemberElder) ? 1 : 0;
    }

    /**
     * 我的家人列表
     * @return
     */
    @Override
    public List<FamilyMemberElderVo> my() {
        Long userId = UserThreadLocal.getUserId();
        List<FamilyMemberElderVo> list = familyMemberElderMapper.selectByMemberId(userId);
        return list;
    }

    /**
     * 分页查询客户老人关联列表
     * @param memberId
     * @return
     */
    @Override
    public List<MemberElderVo> listByPage(Long memberId) {
        return familyMemberElderMapper.listByPage(memberId);
    }

    /**
     * 解绑与老人的关系
     * @param id
     * @return
     */
    @Override
    public int deleteById(Long id) {
        return removeById(id) ? 1 : 0;
    }
}

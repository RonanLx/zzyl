package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.dto.MemberElderDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.service.IDeviceDataService;
import com.zzyl.nursing.service.IDeviceService;
import com.zzyl.nursing.service.IFamilyMemberElderService;
import com.zzyl.nursing.service.IFamilyMemberService;
import com.zzyl.nursing.vo.DeviceDataGraphVo;
import com.zzyl.nursing.vo.FamilyMemberElderVo;
import com.zzyl.nursing.vo.LoginVo;
import com.zzyl.nursing.vo.MemberElderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 老人家属Controller
 * 
 * @author ruoyi
 * @date 2024-09-19
 */
@RestController
@RequestMapping("/member/user")
@Api(tags = "老人家属的接口")
public class FamilyMemberController extends BaseController
{
    @Autowired
    private IFamilyMemberService familyMemberService;


    @PostMapping("/login")
    @ApiOperation("小程序登录")
    public AjaxResult login(@RequestBody UserLoginRequestDto dto){
        LoginVo loginVo = familyMemberService.login(dto);
        return success(loginVo);
    }

    @Autowired
    private IFamilyMemberElderService familyMemberElderService;

    /**
     * 新增客户老人关联记录
     *
     * @param memberElderDto 客户老人关联 DTO
     * @return 操作结果
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增客户老人关联记录")
    public AjaxResult add(@RequestBody MemberElderDto memberElderDto) {
        return toAjax(familyMemberElderService.add(memberElderDto));
    }

    /**
     * 我的家人列表
     *
     * @return 客户老人关联实体类
     */
    @GetMapping("/my")
    @ApiOperation(value = "我的家人列表")
    public R<List<FamilyMemberElderVo>> my() {
        List<FamilyMemberElderVo> memberElders = familyMemberElderService.my();
        return R.ok(memberElders);
    }

    @GetMapping("/list-by-page")
    @ApiOperation(value = "分页查询客户老人关联记录")
    public R<List<MemberElderVo>> listByPage(Integer pageNum, Integer pageSize) {
        startPage();
        Long userId = UserThreadLocal.getUserId();
        List<MemberElderVo> memberElders = familyMemberElderService.listByPage(userId);
        return R.ok(memberElders);
    }

    @DeleteMapping("/deleteById")
    @ApiOperation(value = "根据id删除客户老人关联记录")
    public AjaxResult deleteById(@RequestParam Long id) {
        return toAjax(familyMemberElderService.deleteById(id));
    }

    @Autowired
    private IDeviceService deviceService;

    @GetMapping("/queryServiceProperties/{iotId}")
    public AjaxResult queryDevicePropertyStatus(@PathVariable("iotId") String iotId){
        return deviceService.queryServiceProperties(iotId);
    }

    @Autowired
    private IDeviceDataService deviceDataService;

    @GetMapping("/queryDeviceDataListByDay")
    public AjaxResult queryDeviceDataListByDay(
            String iotId,String functionId,Long startTime,Long endTime
    ){
        List<DeviceDataGraphVo> list = deviceDataService.queryDeviceDataListByDay(iotId,functionId,startTime,endTime);
        return success(list);
    }

    @GetMapping("/queryDeviceDataListByWeek")
    public AjaxResult queryDeviceDataListByWeek(
            String iotId,String functionId,Long startTime,Long endTime
    ){
        List<DeviceDataGraphVo> list = deviceDataService.queryDeviceDataListByWeek(iotId,functionId,startTime,endTime);
        return success(list);
    }

}

package com.zzyl.nursing.controller;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.service.IDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备Controller
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@RestController
@RequestMapping("/nursing/device")
@Api(tags = "设备的接口")
public class DeviceController extends BaseController
{

    @Autowired
    private IDeviceService deviceService;

    /**
     * 查询设备列表
     */
    @GetMapping("/list")
    @ApiOperation("查询设备列表")
    public TableDataInfo list(Device device) {
        startPage();
        List<Device> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    @PostMapping("/syncProductList")
    @ApiOperation(value = "从物联网平台同步产品列表")
    public AjaxResult syncProductList() {
        deviceService.syncProductList();
        return success();
    }

    @GetMapping("/allProduct")
    @ApiOperation(value = "查询所有产品列表")
    public AjaxResult allProduct() {
        return success(deviceService.allProduct());
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册设备")
    public AjaxResult registerDevice(@RequestBody DeviceDto deviceDto) {
        deviceService.registerDevice(deviceDto);
        return success();
    }

    /**
     * 获取设备详细信息
     */
    @GetMapping("/{iotId}")
    @ApiOperation("获取设备详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "iotId", value = "物联网设备id", required = true, dataTypeClass = String.class)
    })
    public AjaxResult getInfo(@PathVariable("iotId") String iotId) {
        return success(deviceService.queryDeviceDetail(iotId));
    }

    /**
     * 查询设备上报数据
     */
    @GetMapping("/queryServiceProperties/{iotId}")
    @ApiOperation("查询设备上报数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "iotId", value = "物联网设备id", required = true, dataTypeClass = String.class)
    })
    public AjaxResult queryServiceProperties(@PathVariable("iotId") String iotId) {
        AjaxResult ajaxResult = deviceService.queryServiceProperties(iotId);
        return ajaxResult;
    }



    @PutMapping
    public AjaxResult edit(@RequestBody DeviceDto deviceDto){
        return toAjax(deviceService.updateDevice(deviceDto));
    }

    @DeleteMapping("/{iotId}")
    @ApiOperation(value = "删除设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "iotId", value = "物联网设备id", required = true, dataTypeClass = String.class)
    })
    public AjaxResult detele(@PathVariable("iotId") String iotId){
        return toAjax(deviceService.deleteDeviceById(iotId));
    }

    @GetMapping("/queryProduct/{productKey}")
    @ApiOperation(value = "查询产品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productKey", value = "产品id", required = true, dataTypeClass = String.class)
    })
    public AjaxResult queryProduct(@PathVariable String productKey){
        return deviceService.queryProduct(productKey);
    }

}

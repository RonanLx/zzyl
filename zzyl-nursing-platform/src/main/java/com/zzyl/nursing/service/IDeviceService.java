package com.zzyl.nursing.service;

import java.util.List;

import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.nursing.domain.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.ProductVo;

/**
 * 设备Service接口
 * 
 * @author ruoyi
 * @date 2025-01-14
 */
public interface IDeviceService extends IService<Device>
{

    /**
     * 查询设备列表
     * 
     * @param device 设备
     * @return 设备集合
     */
    public List<Device> selectDeviceList(Device device);

    /**
     * 修改设备
     * 
     * @param deviceDto 设备
     * @return 结果
     */
    public int updateDevice(DeviceDto deviceDto);

    /**
     * 删除设备信息
     *
     * @return 结果
     */
    public boolean deleteDeviceById(String iotId);

    /**
     * 同步产品列表
     */
    void syncProductList();

    /**
     * 查询所有产品列表
     * @return
     */
    List<ProductVo> allProduct();

    /**
     * 注册设备
     * @param deviceDto
     */
    void registerDevice(DeviceDto deviceDto);

    /**
     * 查询设备详情
     * @param iotId
     * @return
     */
    DeviceDetailVo queryDeviceDetail(String iotId);

    /**
     * 查询设备上报数据
     * @param iotId
     * @return
     */
    AjaxResult queryServiceProperties(String iotId);

    /**
     * 查询产品详情
     * @param productKey
     * @return
     */
    AjaxResult queryProduct(String productKey);
}

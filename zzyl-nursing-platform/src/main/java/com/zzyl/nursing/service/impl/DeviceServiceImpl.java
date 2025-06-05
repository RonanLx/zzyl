package com.zzyl.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.*;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.common.utils.uuid.UUID;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.service.IDeviceService;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.ProductVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

/**
 * 设备Service业务层处理
 *
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@Service
@Slf4j
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper,Device> implements IDeviceService
{

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private IoTDAClient client;

    /**
     * 同步产品列表
     */
    @Override
    public void syncProductList() {
        //请求参数
        ListProductsRequest listProductsRequest = new ListProductsRequest();
        //设置条数
        listProductsRequest.setLimit(50);
        //发起请求
        ListProductsResponse response = client.listProducts(listProductsRequest);
        if(response.getHttpStatusCode() != 200){
            throw new BaseException("物联网接口 - 查询产品，同步失败");
        }
        //存储到redis
        redisTemplate.opsForValue().set(CacheConstants.IOT_ALL_PRODUCT_LIST, JSONUtil.toJsonStr(response.getProducts()));

    }

    /**
     * 查询所有产品列表
     *
     * @return
     */
    @Override
    public List<ProductVo> allProduct() {
        //从redis中查询数据
        String jsonStr = redisTemplate.opsForValue().get(CacheConstants.IOT_ALL_PRODUCT_LIST);
        //如果数据为空，则返回一个空集合
        if(StringUtils.isEmpty(jsonStr)){
            return Collections.emptyList();
        }
        //解析数据，并返回
        return JSONUtil.toList(jsonStr, ProductVo.class);
    }



    /**
     * 注册设备
     * @param deviceDto
     */
    @Override
    public void registerDevice(DeviceDto deviceDto) {
        //判断设备名称是否重复
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Device::getDeviceName, deviceDto.getDeviceName());
        if(count(queryWrapper) > 0){
            throw new BaseException("设备名称已存在，请重新输入");
        }
        //检验设备标识码是否重复
        LambdaQueryWrapper<Device> queryWrapperNodeId = new LambdaQueryWrapper<>();
        queryWrapperNodeId.eq(Device::getNodeId, deviceDto.getNodeId());
        if(count(queryWrapperNodeId) > 0){
            throw new BaseException("设备标识码已存在，请重新输入");
        }

        //校验同一位置是否绑定了同一类产品
        LambdaQueryWrapper<Device> condition = new LambdaQueryWrapper<>();
        condition.eq(Device::getProductKey, deviceDto.getProductKey())
                .eq(Device::getLocationType, deviceDto.getLocationType())
                .eq(Device::getPhysicalLocationType, deviceDto.getPhysicalLocationType())
                .eq(Device::getBindingLocation, deviceDto.getBindingLocation());
        if (count(condition) > 0) {
            throw new BaseException("该老人/位置已绑定该产品，请重新选择");
        }

        //iot中新增设备
        AddDeviceRequest request = new AddDeviceRequest();
        AddDevice body = new AddDevice();
        body.withProductId(deviceDto.getProductKey());
        body.withDeviceName(deviceDto.getDeviceName());
        body.withNodeId(deviceDto.getNodeId());
        request.withBody(body);
        AuthInfo authInfo = new AuthInfo();
        //秘钥
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        authInfo.withSecret(secret);
        body.setAuthInfo(authInfo);
        AddDeviceResponse response;
        try {
            response = client.addDevice(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("物联网接口 - 注册设备，调用失败");
        }

        //设备数据保存到数据库
        //属性拷贝
        Device device = BeanUtil.toBean(deviceDto, Device.class);
        //设备id、设备绑定状态
        device.setIotId(response.getDeviceId());
        //秘钥
        device.setSecret(secret);

        //在数据库中新增设备
        save(device);
    }

    /**
     * 修改设备
     *
     * @param deviceDto 设备
     * @return 结果
     */
    @Override
    public int updateDevice(DeviceDto deviceDto) {

        Device device = getById(deviceDto.getId());
        if(ObjectUtil.isEmpty(device)){
            throw new BaseException("设备不存在");
        }


        UpdateDeviceRequest request = new UpdateDeviceRequest();
        request.withDeviceId(device.getIotId());
        UpdateDevice body = new UpdateDevice();
        body.withDeviceName(deviceDto.getDeviceName());
        request.withBody(body);
        try {
            client.updateDevice(request);
        } catch (Exception e) {
            log.error("物联网接口 - 修改设备，调用失败:{}", e.getMessage());
            throw new BaseException("物联网接口 - 修改设备，调用失败");
        }

        //5.更新数据库中设备信息
        BeanUtil.copyProperties(deviceDto, device);
        return updateById(device) ? 1 : 0;

    }

    /**
     * 查询设备列表
     *
     * @param device 设备
     * @return 设备集合
     */
    @Override
    public List<Device> selectDeviceList(Device device) {
        return deviceMapper.selectDeviceList(device);
    }




    /**
     * 删除设备信息
     *
     * @param iotId
     * @return 结果
     */
    @Override
    public boolean deleteDeviceById(String iotId) {
        DeleteDeviceRequest request = new DeleteDeviceRequest();
        request.withDeviceId(iotId);
        try {
            client.deleteDevice(request);
        } catch (Exception e) {
            log.error("物联网接口 - 删除设备，调用失败:{}", e.getMessage());
            throw new BaseException("物联网接口 - 删除设备，调用失败");
        }

        //从数据库删除设备
        return remove(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, iotId));
    }







    /**
     * 查询设备详情
     *
     * @return
     */
    @Override
    public DeviceDetailVo queryDeviceDetail(String iotId) {
        //查询数据库
        Device device = getOne(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, iotId));
        if (ObjectUtil.isEmpty(device)) {
            return null;
        }

        //调用华为云物联网接口
        ShowDeviceRequest request = new ShowDeviceRequest();
        request.setDeviceId(iotId);
        ShowDeviceResponse response;
        try {
            response = client.showDevice(request);
        } catch (Exception e) {
            log.info("物联网接口 - 查询设备详情，调用失败:{}", e.getMessage());
            throw new BaseException("物联网接口 - 查询设备详情，调用失败");
        }

        //属性拷贝
        DeviceDetailVo deviceVo = BeanUtil.toBean(device, DeviceDetailVo.class);

        deviceVo.setDeviceStatus(response.getStatus());
        String activeTimeStr = response.getActiveTime();
        if(StringUtils.isNotEmpty(activeTimeStr)){
            LocalDateTime activeTime = LocalDateTimeUtil.parse(activeTimeStr, DatePattern.UTC_MS_PATTERN);
            //日期时区转换
            activeTime = activeTime.atZone(ZoneId.from(ZoneOffset.UTC))
                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();
            deviceVo.setActiveTime(activeTime);
        }

        return deviceVo;
    }

    /**
     * 查询设备上报数据
     *
     * @param iotId
     * @return
     */
    @Override
    public AjaxResult queryServiceProperties(String iotId) {

        ShowDeviceShadowRequest request = new ShowDeviceShadowRequest();
        request.setDeviceId(iotId);
        ShowDeviceShadowResponse response = client.showDeviceShadow(request);
        if (response.getHttpStatusCode() != 200) {
            throw new BaseException("物联网接口 - 查询设备上报数据，调用失败");
        }
        List<DeviceShadowData> shadow = response.getShadow();
        if(CollUtil.isEmpty(shadow)){
            List<Object> emptyList = Collections.emptyList();
            return AjaxResult.success(emptyList);
        }
        //返回数据
        JSONObject jsonObject = JSONUtil.parseObj(shadow.get(0).getReported().getProperties());

        List<Map<String,Object>> list = new ArrayList<>();

        //处理上报时间日期
        LocalDateTime activeTime =  LocalDateTimeUtil.parse(shadow.get(0).getReported().getEventTime(), "yyyyMMdd'T'HHmmss'Z'");
        //日期时区转换
        LocalDateTime eventTime = activeTime.atZone(ZoneId.from(ZoneOffset.UTC))
                .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                .toLocalDateTime();

        jsonObject.forEach((k,v)->{
            Map<String,Object> map = new HashMap<>();
            map.put("functionId",k);
            map.put("value",v);
            map.put("eventTime",eventTime);
            list.add(map);
        });

        return AjaxResult.success(list);

    }

    /**
     * 查询产品详情
     * @param productKey
     * @return
     */
    @Override
    public AjaxResult queryProduct(String productKey) {
        //参数校验
        if(StringUtils.isEmpty(productKey)){
            throw new BaseException("请输入正确的参数");
        }
        //调用华为云物联网接口
        ShowProductRequest showProductRequest = new ShowProductRequest();
        showProductRequest.setProductId(productKey);
        ShowProductResponse response;

        try {
            response = client.showProduct(showProductRequest);
        } catch (Exception e) {
            throw new BaseException("查询产品详情失败");
        }
        //判断是否存在服务数据
        List<ServiceCapability> serviceCapabilities = response.getServiceCapabilities();
        if(CollUtil.isEmpty(serviceCapabilities)){
            return AjaxResult.success(Collections.emptyList());
        }

        return AjaxResult.success(serviceCapabilities);
    }
}

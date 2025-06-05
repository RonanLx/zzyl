package com.zzyl.nursing.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.job.vo.IotMsgNotifyData;
import com.zzyl.nursing.mapper.DeviceDataMapper;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.service.IDeviceDataService;
import com.zzyl.nursing.vo.DeviceDataGraphVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备数据Service业务层处理
 * 
 * @author Ronan_JoJo
 * @date 2025-06-05
 */
@Service
@Slf4j
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper,DeviceData> implements IDeviceDataService
{
    @Autowired
    private DeviceDataMapper deviceDataMapper;



    /**
     * 按照天删除设备数据
     */
    @Override
    public void deleteDeviceDataByDay() {
        //获取一个14天之前的日期
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-14);
        //delete from device_data where alarm_time <= '2025-01-15 00:00:00'
        remove(Wrappers.<DeviceData>lambdaQuery().lt(DeviceData::getAlarmTime,localDateTime));
    }

    /**
     * 按天查询设备数据
     * @param iotId
     * @param functionId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<DeviceDataGraphVo> queryDeviceDataListByDay(String iotId, String functionId, Long startTime, Long endTime) {

        //查询数据库
        List<DeviceDataGraphVo> deviceDataGraphVoList = deviceDataMapper.queryDeviceDataListByDay(iotId,functionId,
                LocalDateTimeUtil.of(startTime),
                LocalDateTimeUtil.of(endTime));

        //初始化一个空的，全的数据列表
        //[{deatTime:00:00,dataValue:0.0},{deatTime:01:00,dataValue:0.0},{deatTime:02:00,dataValue:0.0}....]
        List<DeviceDataGraphVo> list = DeviceDataGraphVo.dayInstance(LocalDateTimeUtil.of(startTime));
        //先把数据库中的数据做一个转换，转换为一个map
        //{00:00,89} {02:00,99}  ...
        Map<String, Double> map = deviceDataGraphVoList.stream().collect(Collectors.toMap(DeviceDataGraphVo::getDateTime, DeviceDataGraphVo::getDataValue));
        list.forEach(d->{
            Double val = map.get(d.getDateTime()) == null ? 0.0 : map.get(d.getDateTime());
            d.setDataValue(val);
        });

        return list;
    }

    /**
     * 按周查询设备数据
     * @param iotId
     * @param functionId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<DeviceDataGraphVo> queryDeviceDataListByWeek(String iotId, String functionId, Long startTime, Long endTime) {
        //查询数据库  按周查询的数据
        List<DeviceDataGraphVo> deviceDataGraphVoList = deviceDataMapper.queryDeviceDataListByWeek(iotId,functionId,
                LocalDateTimeUtil.of(startTime),
                LocalDateTimeUtil.of(endTime));

        //初始化一个空的，全的数据列表
        //[{deatTime:00:00,dataValue:0.0},{deatTime:01:00,dataValue:0.0},{deatTime:02:00,dataValue:0.0}....]
        List<DeviceDataGraphVo> list = DeviceDataGraphVo.weekInstance(LocalDateTimeUtil.of(startTime));
        //先把数据库中的数据做一个转换，转换为一个map
        //{00:00,89} {02:00,99}  ...
        Map<String, Double> map = deviceDataGraphVoList.stream().collect(Collectors.toMap(DeviceDataGraphVo::getDateTime, DeviceDataGraphVo::getDataValue));
        list.forEach(d->{
            Double val = map.get(d.getDateTime()) == null ? 0.0 : map.get(d.getDateTime());
            d.setDataValue(val);
        });

        return list;
    }

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * 批量保存数据
     * @param iotMsgNotifyData
     */
    @Override
    public void batchInsertDeviceData(IotMsgNotifyData iotMsgNotifyData) {
        //判断设备是否存在
        String iotId = iotMsgNotifyData.getHeader().getDeviceId();
        Device device = deviceMapper.selectOne(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, iotId));
        if(device == null){
            //日志的记录，方便后期查找问题
            log.error("设备不存在，iotId:{}",iotId);
            return;
        }
        //获取到设备上报的数据，一个设备可以有多个service
        iotMsgNotifyData.getBody().getServices().forEach(s->{

            //所有数据都已经装入到map中
            Map<String, Object> properties = s.getProperties();
            if (ObjectUtil.isEmpty(properties)){
                return;
            }
            //处理上报时间日期
            LocalDateTime eventTime =  LocalDateTimeUtil.parse(s.getEventTime(), "yyyyMMdd'T'HHmmss'Z'");
            //日期时区转换
            LocalDateTime alarmTime = eventTime.atZone(ZoneId.from(ZoneOffset.UTC))
                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();

            //转入多个设备数据
            List<DeviceData> deviceDataList = new ArrayList<>();

            //遍历map集合，批量新增数据
            properties.forEach((key, value)->{
                DeviceData deviceData = DeviceData.builder()
                        .iotId(iotId)
                        .deviceName(device.getDeviceName())
                        .productKey(device.getProductKey())
                        .productName(device.getProductName())
                        .functionId(key)
                        .accessLocation(device.getRemark())
                        .locationType(device.getLocationType())
                        .physicalLocationType(device.getPhysicalLocationType())
                        .deviceDescription(device.getDeviceDescription())
                        .alarmTime(alarmTime)
                        .dataValue(value + "")
                        .build();
                deviceDataList.add(deviceData);

            });

            //批量保存
            saveBatch(deviceDataList);

            //存储到redis中deviceDataList
            //hash 结构来进行存储  大key  小key  value
            redisTemplate.opsForHash().put(CacheConstants.IOT_DEVICE_LAST_DATA,iotId, JSONUtil.toJsonStr(deviceDataList));

        });

    }

    /**
     * 查询设备数据
     * 
     * @param id 设备数据主键
     * @return 设备数据
     */
    @Override
    public DeviceData selectDeviceDataById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询设备数据列表
     * 
     * @param deviceData 设备数据
     * @return 设备数据
     */
    @Override
    public List<DeviceData> selectDeviceDataList(DeviceData deviceData)
    {
        return deviceDataMapper.selectDeviceDataList(deviceData);
    }

    /**
     * 新增设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    @Override
    public int insertDeviceData(DeviceData deviceData)
    {
        return save(deviceData)?1:0;
    }

    /**
     * 修改设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    @Override
    public int updateDeviceData(DeviceData deviceData)
    {
        return updateById(deviceData)?1:0;
    }

    /**
     * 批量删除设备数据
     * 
     * @param ids 需要删除的设备数据主键
     * @return 结果
     */
    @Override
    public int deleteDeviceDataByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除设备数据信息
     * 
     * @param id 设备数据主键
     * @return 结果
     */
    @Override
    public int deleteDeviceDataById(Long id)
    {
        return removeById(id)?1:0;
    }

}

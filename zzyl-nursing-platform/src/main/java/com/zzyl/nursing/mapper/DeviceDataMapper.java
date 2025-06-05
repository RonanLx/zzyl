package com.zzyl.nursing.mapper;

import java.time.LocalDateTime;
import java.util.List;
import com.zzyl.nursing.domain.DeviceData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.vo.DeviceDataGraphVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备数据Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Mapper
public interface DeviceDataMapper extends BaseMapper<DeviceData>
{
    /**
     * 查询设备数据
     * 
     * @param id 设备数据主键
     * @return 设备数据
     */
    public DeviceData selectDeviceDataById(Long id);

    /**
     * 查询设备数据列表
     * 
     * @param deviceData 设备数据
     * @return 设备数据集合
     */
    public List<DeviceData> selectDeviceDataList(DeviceData deviceData);

    /**
     * 新增设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    public int insertDeviceData(DeviceData deviceData);

    /**
     * 修改设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    public int updateDeviceData(DeviceData deviceData);

    /**
     * 删除设备数据
     * 
     * @param id 设备数据主键
     * @return 结果
     */
    public int deleteDeviceDataById(Long id);

    /**
     * 批量删除设备数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceDataByIds(Long[] ids);

    List<DeviceDataGraphVo> queryDeviceDataListByDay(
            @Param("iotId") String iotId,
            @Param("functionId") String functionId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


    List<DeviceDataGraphVo> queryDeviceDataListByWeek(
            @Param("iotId") String iotId,
            @Param("functionId") String functionId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}

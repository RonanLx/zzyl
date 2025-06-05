package com.zzyl.nursing.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.domain.Room;
import com.zzyl.nursing.mapper.RoomMapper;
import com.zzyl.nursing.service.IRoomService;
import com.zzyl.nursing.vo.DeviceInfo;
import com.zzyl.nursing.vo.RoomVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 房间Service业务层处理
 *
 * @author ruoyi
 * @date 2024-04-26
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {
    @Autowired
    private RoomMapper roomMapper;

    /**
     * 查询房间
     *
     * @param id 房间主键
     * @return 房间
     */
    @Override
    public Room selectRoomById(Long id) {
        return getById(id);
    }

    /**
     * 查询房间列表
     *
     * @param room 房间
     * @return 房间
     */
    @Override
    public List<Room> selectRoomList(Room room) {
        return roomMapper.selectRoomList(room);
    }

    /**
     * 新增房间
     *
     * @param room 房间
     * @return 结果
     */
    @Override
    public int insertRoom(Room room) {
        return save(room) ? 1 : 0;
    }

    /**
     * 修改房间
     *
     * @param room 房间
     * @return 结果
     */
    @Override
    public int updateRoom(Room room) {
        return updateById(room) ? 1 : 0;
    }

    /**
     * 批量删除房间
     *
     * @param ids 需要删除的房间主键
     * @return 结果
     */
    @Override
    public int deleteRoomByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 根据楼层 id 获取房间视图对象列表
     *
     * @param floorId
     * @return
     */
    @Override
    public List<RoomVo> getRoomsByFloorId(Long floorId) {
        return roomMapper.selectByFloorId(floorId);
    }


    /**
     * 获取所有房间（负责老人）
     *
     * @param floorId
     * @return
     */
    @Override
    public List<RoomVo> getRoomsWithNurByFloorId(Long floorId) {
        return roomMapper.selectByFloorIdWithNur(floorId);
    }

    /**
     * 根据房间id查询楼层、房间、价格
     * @param id
     * @return
     */
    @Override
    public RoomVo getRoomById(Long id) {
        return roomMapper.getRoomById(id);
    }

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 根据楼层id查询房间、设备、设备的数据
     * @param floorId
     * @return
     */
    @Override
    public List<RoomVo> getRoomsWithDeviceByFloorId(Long floorId) {
        //定义个持久层mapper,查询房间、床位、老人、设备，不包含设备数据（redis）
        List<RoomVo> list = roomMapper.getRoomsWithDeviceByFloorId(floorId);
        //遍历房间
        list.forEach(roomVo -> {
            //找到房间的内设备
            List<DeviceInfo> deviceVos = roomVo.getDeviceVos();
            deviceVos.forEach(deviceInfo -> {
                //通过设备id查询redis进行匹配，如果匹配到就给deviceInfo类中的deviceDataVos赋值
                String iotId = deviceInfo.getIotId();
                String jsonStr = (String) redisTemplate.opsForHash().get(CacheConstants.IOT_DEVICE_LAST_DATA, iotId);
                if(StringUtils.isEmpty(jsonStr)){
                    return;
                }
                //不为空，要把json数据转换为list集合装入到deviceInfo
                deviceInfo.setDeviceDataVos(JSONUtil.toList(jsonStr, DeviceData.class));
            });
            //找到房间内的床位和设备
            roomVo.getBedVoList().forEach(bedVo -> {
                //直接找的是  床位对应的设备信息
                bedVo.getDeviceVos().forEach(deviceInfo -> {
                    //通过设备id查询redis进行匹配，如果匹配到就给deviceInfo类中的deviceDataVos赋值
                    String iotId = deviceInfo.getIotId();
                    String jsonStr = (String) redisTemplate.opsForHash().get(CacheConstants.IOT_DEVICE_LAST_DATA, iotId);
                    if(StringUtils.isEmpty(jsonStr)){
                        return;
                    }
                    //不为空，要把json数据转换为list集合装入到deviceInfo
                    deviceInfo.setDeviceDataVos(JSONUtil.toList(jsonStr, DeviceData.class));
                });
            });

        });

        return list;
    }
}

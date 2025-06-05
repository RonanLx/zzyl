package com.zzyl.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.domain.AlertData;
import com.zzyl.nursing.domain.AlertRule;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.mapper.AlertRuleMapper;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.service.IAlertDataService;
import com.zzyl.nursing.service.IAlertRuleService;
import com.zzyl.nursing.vo.AlertNotifyVo;
import com.zzyl.system.mapper.SysUserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 报警规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-17
 */
@Service
@Slf4j
public class AlertRuleServiceImpl extends ServiceImpl<AlertRuleMapper,AlertRule> implements IAlertRuleService
{
    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 过滤数据
     */
    @Override
    public void alertFilter() {
        //查询所有的规则
        long count = count(Wrappers.<AlertRule>lambdaQuery().eq(AlertRule::getStatus, 1));

        //判断规则是否为空，如果为空，则结束
        if(count <= 0){
            return;
        }

        //获取设备上报的数据  redis
        List<Object> values = redisTemplate.opsForHash().values(CacheConstants.IOT_DEVICE_LAST_DATA);

        //判断数据是否为空，为空就结束
        if(CollUtil.isEmpty(values)){
            return;
        }

        //格式化上报的数据
        List<DeviceData> deviceDataList = new ArrayList<>();
        values.forEach(v-> deviceDataList.addAll(JSONUtil.toList(v.toString(),DeviceData.class)));

        //进一步的数据过滤
        deviceDataList.forEach(deviceData -> alertFilter(deviceData));
    }

    /**
     * 过滤数据
     * @param deviceData   上报的数据
     */
    private void alertFilter(DeviceData deviceData) {

        //如果当前上报的数据超过了1分钟，则没必要继续过滤了
        LocalDateTime alarmTime = deviceData.getAlarmTime();
        long between = LocalDateTimeUtil.between(alarmTime, LocalDateTime.now(), ChronoUnit.SECONDS);
        if(between > 60){
            return;
        }

        //查询所有的该产品下的规则
        List<AlertRule> allRules = list(Wrappers.<AlertRule>lambdaQuery()
                .eq(AlertRule::getProductKey, deviceData.getProductKey())
                .eq(AlertRule::getFunctionId, deviceData.getFunctionId())
                .eq(AlertRule::getIotId, "-1")
                .eq(AlertRule::getStatus, 1));

        //查询指定设备的规则
        List<AlertRule> iotRules = list(Wrappers.<AlertRule>lambdaQuery()
                .eq(AlertRule::getProductKey, deviceData.getProductKey())
                .eq(AlertRule::getFunctionId, deviceData.getFunctionId())
                .eq(AlertRule::getIotId, deviceData.getIotId())
                .eq(AlertRule::getStatus, 1));

        //合并规则
        Collection<AlertRule> alertRules = CollUtil.addAll(allRules, iotRules);
        //判断集合是否为空
        if(CollUtil.isEmpty(alertRules)){
            return;
        }

        //获取每一条规则和对应的报警数据进行匹配
        alertRules.forEach(alertRule -> deviceDataAlarmHandler(alertRule,deviceData));


    }

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Value("${alert.deviceMaintainerRole}")
    private String deviceMaintainerRole;

    @Value("${alert.managerRole}")
    private String managerRole;

    /**
     * 获取每一条规则和对应的报警数据进行匹配
     * @param rule     报警规则  持续周期  和   沉默周期     操作符  阈值
     * @param deviceData   上报的数据
     */
    private void deviceDataAlarmHandler(AlertRule rule, DeviceData deviceData) {
        //判断上报的数据是否在规则的生效时段内   ~
        String[] split = rule.getAlertEffectivePeriod().split("~");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        //获取上报时间
        LocalTime localTime = deviceData.getAlarmTime().toLocalTime();
        //如果不在上报时间内，则结束请求  8-10
        if(localTime.isBefore(startTime) || localTime.isAfter(endTime)){
            return;
        }

        //计数的key
        String iotId = deviceData.getIotId();
        String aggCountKey = CacheConstants.IOT_COUNT_ALERT + iotId + ":" + deviceData.getFunctionId()+ ":" + rule.getId();

        //判读上报的时间是否符合规则定义的阈值   55   60<
        //比较，两个值的大小，分别是上报的数据，规则中定义的阈值
        //两个参数（x,y） x==y  返回0   x<y 返回-1   x>y 返回1
        int compare = NumberUtil.compare(Double.valueOf(deviceData.getDataValue()), rule.getValue());
        if(rule.getOperator().equals(">=") && compare >= 0 ||
                rule.getOperator().equals("<") && compare < 0){
            log.info("当前上报的数据符合报警规则，需要处理");
        }else {
            //正常的数据，下次都要重新计算次数，删除计数的数据
            redisTemplate.delete(aggCountKey);
            return;
        }

        //代码能走这，说明已经找到报警的数据了   （ 持续周期   沉默周期  ）
        //持续周期    3  连续3分钟，都出现了报警，才会产生真正的报警
        //使用redis存储次数，每次累加

        //沉默周期的key
        String silenceKey = CacheConstants.IOT_SILENT_ALERT + iotId + ":" + deviceData.getFunctionId()+ ":" + rule.getId();

        //判断沉默周期是否存在，如果存在，说明在持续周期内，就结束请求
        String silenceData = redisTemplate.opsForValue().get(silenceKey);
        if(StringUtils.isNotEmpty(silenceData)){
            return;
        }

        //先获取redis中的持续周期数据

        String aggData = redisTemplate.opsForValue().get(aggCountKey);
        int count = StringUtils.isEmpty(aggData) ? 1 : Integer.parseInt(aggData) + 1;
        //判断count与持续周期是否相等，如果相等就触发报警，不相等:把这个count重新保存在redis中
        if(ObjectUtil.notEqual(count, rule.getDuration())){
            redisTemplate.opsForValue().set(aggCountKey,count+"");
            return;
        }

        //删除统计的数据
        redisTemplate.delete(aggCountKey);
        //新增沉默周期，在沉默周期内，不要过滤报警
        redisTemplate.opsForValue().set(silenceKey,"1",rule.getAlertSilentPeriod(), TimeUnit.MINUTES);


        //TODO 符合条件，说明符合了持续周期，要产生报警数据,保存报警数据
        //找到需要通知的人，才能保存报警数据
        List<Long> userIds = new ArrayList<>();
        if(rule.getAlertDataType().equals(0)){
            //找的是，老人异常数据，通知老人对应的护理员
            //如果是手表，直接可以到设备表中找到老人的id,通过老人的id找到对应的护理员
            if(deviceData.getLocationType().equals(0)){//老人的随身设备
                userIds = deviceMapper.selectNursingIdByIotIdWithElder(deviceData.getIotId());
            }else if(deviceData.getLocationType().equals(1) && deviceData.getPhysicalLocationType().equals(2)){
                //如果是睡眠检测带，只能找到床位，通过床位找到老人的id，通过老人的id找到对应的护理员
                userIds = deviceMapper.selectNursingIdByIotIdWithBed(deviceData.getIotId());
            }

        }else {
            //设备异常数据，要找维修人员，或者行政人员
            userIds = sysUserRoleMapper.selectByRoleName(deviceMaintainerRole);
        }

        //找到超级管理员进行通知
        List<Long> list = sysUserRoleMapper.selectByRoleName(managerRole);

        //合并数据
        Collection<Long> allUserIds = CollUtil.addAll(userIds, list);
        //不能包含重复的数据
        allUserIds = CollUtil.distinct(allUserIds);
        //保存的报警数据了
        List<AlertData> alertDataList = insertAlertData(allUserIds, rule, deviceData);

        //调用websocket推送消息
        websocketNotity(alertDataList.get(0), rule, allUserIds);

    }

    /**
     * websocket推送消息
     * @param alertData
     * @param rule
     * @param allUserIds
     */
    private void websocketNotity(AlertData alertData, AlertRule rule, Collection<Long> allUserIds) {

        //属性拷贝
        AlertNotifyVo alertNotifyVo = BeanUtil.toBean(alertData, AlertNotifyVo.class);
        alertNotifyVo.setFunctionName(rule.getFunctionName());
        alertNotifyVo.setAlertDataType(rule.getAlertDataType());
        alertNotifyVo.setNotifyType(1);
        //向指定的人推送消息
        //webSocketServer.sendMessageToConsumer(alertNotifyVo,allUserIds);

    }

    @Autowired
    private IAlertDataService alertDataService;

    /**
     * 批量保存报警数据
     * @param allUserIds
     * @param rule
     * @param deviceData
     */
    private List<AlertData> insertAlertData(Collection<Long> allUserIds, AlertRule rule, DeviceData deviceData) {

        //属性拷贝
        AlertData alertData = BeanUtil.toBean(deviceData, AlertData.class);
        //属性补全
        alertData.setAlertRuleId(rule.getId());
        //报警原因
        alertData.setAlertReason(rule.getFunctionName() + rule.getOperator() + rule.getValue() + "，持续" + rule.getDuration() + "分钟，沉默" + rule.getAlertSilentPeriod() + "分钟");
        //状态
        alertData.setStatus(0);
        //类型
        alertData.setType(rule.getAlertDataType());

        List<AlertData> list = allUserIds.stream().map(userId -> {
            AlertData dbAlertData = BeanUtil.toBean(alertData, AlertData.class);
            dbAlertData.setUserId(userId);
            dbAlertData.setId(null);
            return dbAlertData;

        }).collect(Collectors.toList());
        alertDataService.saveBatch(list);
        return list;
    }

    /**
     * 查询报警规则
     * 
     * @param id 报警规则主键
     * @return 报警规则
     */
    @Override
    public AlertRule selectAlertRuleById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询报警规则列表
     * 
     * @param alertRule 报警规则
     * @return 报警规则
     */
    @Override
    public List<AlertRule> selectAlertRuleList(AlertRule alertRule)
    {
        return alertRuleMapper.selectAlertRuleList(alertRule);
    }

    /**
     * 新增报警规则
     * 
     * @param alertRule 报警规则
     * @return 结果
     */
    @Override
    public int insertAlertRule(AlertRule alertRule)
    {
        return save(alertRule)?1:0;
    }

    /**
     * 修改报警规则
     * 
     * @param alertRule 报警规则
     * @return 结果
     */
    @Override
    public int updateAlertRule(AlertRule alertRule)
    {
        return updateById(alertRule)?1:0;
    }

    /**
     * 批量删除报警规则
     * 
     * @param ids 需要删除的报警规则主键
     * @return 结果
     */
    @Override
    public int deleteAlertRuleByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除报警规则信息
     * 
     * @param id 报警规则主键
     * @return 结果
     */
    @Override
    public int deleteAlertRuleById(Long id)
    {
        return removeById(id)?1:0;
    }

}

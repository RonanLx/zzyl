package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IDeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeviceDataRemoveJob {

    @Autowired
    private IDeviceDataService deviceDataService;

    public void removeDeviceDataByDay() {
        deviceDataService.deleteDeviceDataByDay();
        log.info("删除设备数据成功......");
    }
}

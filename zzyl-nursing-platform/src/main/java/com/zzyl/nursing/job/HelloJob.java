package com.zzyl.nursing.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HelloJob {

//    @Scheduled(cron = "*/5 * * * * ?")//每5秒执行一次
    public void hello(){
        System.out.println("hello world"+ LocalDateTime.now());
    }

    public void myJob(){
        System.out.println("myjob----"+ LocalDateTime.now());
    }
}

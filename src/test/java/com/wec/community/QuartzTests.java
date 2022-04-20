package com.wec.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTests {

    @Autowired
    private Scheduler scheduler;//注入调度器


    //删除刚刚测试的job
    @Test
    public void testDeleteJob(){
        try {
            boolean result = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));//两个参数分别为job名字和组名，由这两个确定唯一的job
            System.out.println(result);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}

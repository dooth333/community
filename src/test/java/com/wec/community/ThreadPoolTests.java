package com.wec.community;


import com.wec.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);//初始化后包括5个线程

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;


    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //1. JDK普通线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    //2. 定时任务线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);// TimeUnit.MILLISECONDS单位ms
        sleep(30000);
    }

    //3. Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.execute(task);
        }
        sleep(10000);
    }


    //4. Spring可执行定时任务的线程池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis()+10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(30000);

    }

    //5.spring普通线程池的简化（加注解@Async）
    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }
        sleep(30000);
    }

    //6. Spring可执行定时任务的线程池简化（加注解 @Scheduled）
    @Test
    public void testThreadPoolTaskSchedulerSimple(){
        sleep(30000);
    }


}

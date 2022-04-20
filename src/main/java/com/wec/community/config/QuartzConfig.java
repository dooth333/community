package com.wec.community.config;

import com.wec.community.quartz.AlphaJob;
import com.wec.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置 -> 数据库 ->调用
@Configuration
public class QuartzConfig {

    //FactoryBean可简化bean的实例化过程
    //1.通过FactoryBean封装了某一些Bean的实例化过程
    //2.将FactoryBean装配到spring容器中
    //3.将FactoryBean注入到其他的bean中
    //4.该bean的得到的是FactoryBean所管理的对象实例

    //配置JobDetail
    //@Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");//名字独一无二
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);//设置是否长久的保存
        factoryBean.setRequestsRecovery(true);//是否是恢复的
        return factoryBean;
    }

    //配置Trigger(SimpleTriggerFactoryBean或CronTriggerFactoryBean)
    //@Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail){//参数名与对应的JobDetail的bean的方法名字保持一致有利于优先调用，防止冲突
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);//要对哪个JobDetail生效
        factoryBean.setName("alphaTrigger");//设置名字
        factoryBean.setGroup("alphaTriggerGroup");//设置组
        factoryBean.setRepeatInterval(3000);//频率，多长时间执行一次（ms）
        factoryBean.setJobDataMap(new JobDataMap());//存一些Job的状态，通过new JobDataMap()
        return factoryBean;
    }


    //刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);//该配置适用于哪个Job
        factoryBean.setName("postScoreRefreshJob");//名字独一无二
        factoryBean.setGroup("communityJobGroup");//所属分组
        factoryBean.setDurability(true);//设置是否长久的保存
        factoryBean.setRequestsRecovery(true);//是否是恢复的
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){//参数名与对应的JobDetail的bean的方法名字保持一致有利于优先调用，防止冲突
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);//要对哪个JobDetail生效
        factoryBean.setName("postScoreRefreshJobDetailTrigger");//设置名字
        factoryBean.setGroup("communityTriggerGroup");//设置组
        factoryBean.setRepeatInterval(1000 * 60 * 5);//频率，多长时间执行一次（ms）
        factoryBean.setJobDataMap(new JobDataMap());//存一些Job的状态，通过new JobDataMap()
        return factoryBean;
    }

}

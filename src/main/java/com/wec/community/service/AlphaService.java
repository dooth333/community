package com.wec.community.service;

import com.wec.community.dao.AlphaDao;
import com.wec.community.dao.DiscussPostMapper;
import com.wec.community.dao.UserMapper;
import com.wec.community.entity.DiscussPost;
import com.wec.community.entity.User;
import com.wec.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")  prototype表示设置为多例的，默认单例
public class AlphaService {

    private static Logger logger = LoggerFactory.getLogger(AlphaService.class);
    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    //编程式事务注入的bean
    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }
    @PreDestroy
    public void destory(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        String select = alphaDao.select();
        return select;
    }


    //导import org.springframework.transaction.annotation.Transactional;的包
    //添加isolation = Isolation.READ_COMMITTED选择级别，不加默认级别
    //propagation = Propagation.REQUIRED的几个传播机制：3个常用的（解决几个事务交叉的问题）
    // REQUIRED ：支持当前事务（外部事务），如果外部事务不存在就创建新事物（比如a调用我现在这个事务，a即是外部事物，若a没有被调用即没有外部事物，a自己创建事务）
    // REQUIRES_NEW ：创建一个新的事务暂停外部事务
    // NESTED ：如果当前存在事务（外部事务），则嵌套在外部事务中执行（有独立的提交和回滚），否则就创建新事物
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("https://image.nowcoder.com/header/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("HelloWord");
        post.setContent("新人报到");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user = new User();
                user.setUsername("bate");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("bate@qq.com");
                user.setHeaderUrl("https://image.nowcoder.com/header/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("nihao");
                post.setContent("新人报到aaaaa");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "OK";
            }
        });
    }


    //该方法在多线程环境下，被异步调用
    //@Async
    public void execute1(){
        logger.debug("execute1");
    }

    //@Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2(){
        logger.debug("execute2");
    }

}

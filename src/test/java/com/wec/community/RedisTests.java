package com.wec.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        //String类型访问
        String redisKey = "test:count"; //String类型
        redisTemplate.opsForValue().set(redisKey,1);//String类型存入
        System.out.println(redisTemplate.opsForValue().get(redisKey));//String类型读取
        redisTemplate.opsForValue().increment(redisKey); //String类型加1
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        redisTemplate.opsForValue().decrement(redisKey);//String类型减1
        System.out.println(redisTemplate.opsForValue().get(redisKey));
    }

    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1); //哈希类型存入
        redisTemplate.opsForHash().put(redisKey,"username","lisi"); //哈希类型存入
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username")); //哈希类型取出
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
    }

    @Test
    public void testLists(){
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);//列表类型存入
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        redisTemplate.opsForList().leftPush(redisKey,104);//列表类型存入，左近右出 为 104 103 102 101
        System.out.println(redisTemplate.opsForList().size(redisKey)); //读取列表元素个数
        System.out.println(redisTemplate.opsForList().index(redisKey,0)); //读取列表第i个元素，注意左进右出
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));//查取范围内的元素
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//从左边弹出
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//从左边弹出
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//从左边弹出
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));//从右边弹出
    }

    @Test
    public void testSets(){
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey,"刘备","关关羽","张飞","赵云"); //无序列表存入
        System.out.println( redisTemplate.opsForSet().size(redisKey)); //查询无序列表的元素个数
        System.out.println( redisTemplate.opsForSet().members(redisKey)); //列出所有元素
        System.out.println( redisTemplate.opsForSet().pop(redisKey)); //随机弹出一个元素，用于抽奖
        System.out.println( redisTemplate.opsForSet().members(redisKey)); //列出所有元素
    }

    @Test
    public void testSortedSet(){
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey,"唐僧",90); //有序列表的存入
        redisTemplate.opsForZSet().add(redisKey,"孙悟空",80); //有序列表的存入
        redisTemplate.opsForZSet().add(redisKey,"八戒",60); //有序列表的存入
        redisTemplate.opsForZSet().add(redisKey,"撒僧",75);
        redisTemplate.opsForZSet().add(redisKey,"白龙马",70);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"八戒")); //查看分数
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"八戒"));  //反向排名
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2)); //查看范围内的元素
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2)); //取出反向排名范围内的元素
    }

    @Test
    public void testKeys(){
        redisTemplate.delete("test:user"); //删除key
        System.out.println(redisTemplate.hasKey("test:user")); //判断key是否存在
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);//设置过期时间
    }

    //多次访问同一key
    @Test
    public void testBoundOperation(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);//绑定value(String)类型的Key,其他类型替换value之后调用不用再传入key,其他和上述操作类似
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    //redis事务（把同一事务的所有操作先放在一个队列中，等所有一块执行，所以不要在事务中间进行查询，他也会等到最后一起查询，把查询放到事务之前或提交之后查询）
    //编程式事务
    @Test
    public void testTransaction(){
        Object obj =  redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //事务
                String redisKey = "text:tx";
                operations.multi();//启用事务
                operations.opsForSet().add(redisKey,"张三");
                operations.opsForSet().add(redisKey,"李四");
                operations.opsForSet().add(redisKey,"王大");
                System.out.println(operations.opsForSet().members((redisKey))); //[] 所以中间不用做查询
                return operations.exec();//提交事务
            }
        });
        System.out.println(obj); //[1, 1, 1, [张三, 李四, 王大]] 1表示影响的行数
    }
}

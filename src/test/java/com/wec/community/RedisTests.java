package com.wec.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
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


   //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog() {
        String redisKey = "test:hll:01";

        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for (int i = 1; i <= 10000; i++) {
            int r = (int) (Math.random()*10000+1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    //讲三组数据合并，再统计合并后的重复数据的独立总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 = "test:hll:03";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);//第一个为接收合并后的数据，后面参数为多个要合并的数据
        Long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }


    //
    @Test
    public void testBitMap(){

        String redisKey = "test:bm:01";
        //记录(opsForValue()用字符串类型的，只不过时按位操作)
        redisTemplate.opsForValue().setBit(redisKey,1,true);//默认为false,所以false不用存
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);

        //查某一位结果
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));

        //统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //redisConnection为redis底层连接
                return redisConnection.bitCount(redisKey.getBytes());//统计redisKey.getBytes()也就是redisKey中1的个数
            }
        });
        System.out.println(obj);
    }

    //统计三组数据的布尔值，并对这三组数据进行OR运算
    @Test
    public void testBitMapOperation(){
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2,0,true);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,3,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);

        String redisKey = "test:bm:or";//存or运算结果
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //connection为redis底层连接
                connection.bitOp(RedisStringCommands.BitOperation.OR//指定运算符
                        ,redisKey.getBytes()//存计算结果
                        ,redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes() );
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));

    }



}

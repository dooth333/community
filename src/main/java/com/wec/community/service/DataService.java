package com.wec.community.service;

import com.wec.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //讲指定的ip计入UA
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计指定日期范围内的UV
    public long calculateUV(Date start,Date end){
        if (start == null || end == null){
            throw  new IllegalArgumentException("参数不能为空");
        }
        //整理改日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){ //这个时间小于等于end日期时满足循环
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);//加一天
        }

        //合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(df.format(start),df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());//keyList.toArray()把集合转换为数组

        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //统计活跃用户，将指定活跃用户存入DAU
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true); //把用户id那一位变为true，标记为活跃用户
    }

    //统计日期指定范围内的DAU,在这之中任意一天活跃则为活跃
    public long calculateDAU(Date start,Date end){
        if (start == null || end == null){
            throw  new IllegalArgumentException("参数不能为空");
        }

        //整理改日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){ //这个时间小于等于end日期时满足循环
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());//把key转换为字节数组存入集合
            calendar.add(Calendar.DATE,1);//加一天
        }
        //进行or运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));

                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}

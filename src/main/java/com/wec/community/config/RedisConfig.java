package com.wec.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String ,Object> redisTemplate(RedisConnectionFactory factory){//注入连接工厂
        //实例化
        RedisTemplate<String ,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);//把工厂给 template

        // 设置 key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置 value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化公式
        template.setHashValueSerializer(RedisSerializer.json());


        //让template生效
        template.afterPropertiesSet();
        return template;
    }
}

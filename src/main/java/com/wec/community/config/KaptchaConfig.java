package com.wec.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration //表示这是一个配置类
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){//Producer实例可以用于生成验证码
        //配置验证码参数
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");//配置验证码图片的宽度
        properties.setProperty("kaptcha.image.height","40");//配置验证码图片的高度
        properties.setProperty("kaptcha.textproducer.font.size","40");//配置验证码字号
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");//配置验证码字号
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");//配置验证码生成字的范围
        properties.setProperty("kaptcha.textproducer.char.length","4");//配置验证码生成验证码的字数
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");//配置验证码要采用那个干扰类
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}

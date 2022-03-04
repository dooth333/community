package com.wec.community.config;

import com.wec.community.controller.interceptor.Alphalnterceptor;
import com.wec.community.controller.interceptor.LoginRequiredInterceptor;
import com.wec.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //表示是配置类
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private Alphalnterceptor alphalnterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphalnterceptor)//增加拦截
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")//设置排除拦截的目录(/**表示目录下所有文件夹)
                .addPathPatterns("/register","/login");//设置拦截的目录

        registry.addInterceptor(loginTicketInterceptor)//增加拦截
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");//设置排除拦截的目录(/**表示目录下所有文件夹)
                //设置拦截全部请求（不设置add）
        registry.addInterceptor(loginRequiredInterceptor)//增加拦截
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");//设置排除拦截的目录(/**表示目录下所有文件夹)
        //设置拦截全部请求（不设置add）
    }
}

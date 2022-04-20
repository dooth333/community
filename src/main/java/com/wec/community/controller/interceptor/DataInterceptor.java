package com.wec.community.controller.interceptor;

import com.wec.community.entity.User;
import com.wec.community.service.DataService;
import com.wec.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class DataInterceptor implements HandlerInterceptor {//使用拦截器来统计，

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    //在每次请求之间就进行统计
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        //统计DAU
        User user = hostHolder.getUser();
        if (user != null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }

}

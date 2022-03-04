package com.wec.community.controller.interceptor;

import com.wec.community.annotation.LoginRequired;
import com.wec.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
/***
 * 对加了自定义注解LoginRequired的进行判断，不合理的进行拦截并重定向
 */
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){//判断拦截的是不是方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//取注解
            if (loginRequired != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath()+"/login"); //重定向登录页面
                return false;
            }
        }
        return true;
    }
}

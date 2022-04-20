package com.wec.community.config;


import com.wec.community.util.CommunityConstant;
import com.wec.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略静态资源，不要拦截
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        //需要添加权限的路径(以下只要登录即可)
                        "/user/setting",
                        "/user/upload",
                        "/user/setPassword",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        //以下权限任意一个就可访问
                        AUTHORITY_USER,
                        AUTHORITY_MODERATOR,
                        AUTHORITY_ADMIN
                )
                .antMatchers(
                        //版主的单独功能
                        "/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        //管理员才能用的请求
                        "/discuss/delete",
                        "/data/**"
                ).hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()//其他任何请求都可以任意访问
                .and().csrf().disable();//不启用CSRF

        //权限不够时:
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没登陆时如何处理
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");//获取请求方式
                        if ("XMLHttpRequest".equals(xRequestedWith)){
                            //异步请求，返回json字符串
                            response.setContentType("application/plain;charset=utf-8");//返回的类型
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你还没有登录"));
                        }else {
                            //同步请求，直接重定向到登录页面
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //登录了但是权限不足如何处理
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");//获取请求方式
                        if ("XMLHttpRequest".equals(xRequestedWith)){
                            //异步请求，返回json字符串
                            response.setContentType("application/plain;charset=utf-8");//返回的类型
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你没有访问此功能的权限"));
                        }else {
                            //同步请求，直接重定向到登录页面
                            response.sendRedirect(request.getContextPath()+"/denied");
                        }
                    }
                });

        //Security底层默认拦截/logout请求，并进行退出处理
        //覆盖他默认逻辑，才能执行我们自己的退出代码
        http.logout().logoutUrl("securityLogout"); //改完之后就不会拦截"/logout",而是去拦截我们设置的URL,进而绕过，使用我们自己
    }
}

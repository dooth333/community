package com.wec.community.util;

import com.wec.community.entity.User;
import org.springframework.stereotype.Component;

/***
 * 持有用户的信息，代替session对象
 * 用隔离线程存储信息
 * 这样可以防止多个人访问时多线程冲突问题
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }

    //清理
    public void clear(){
        users.remove();
    }
}

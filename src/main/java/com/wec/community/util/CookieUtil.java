package com.wec.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * 封装了查询通过cookie名直接查询value
 */
public class CookieUtil {

    public static String  getValue(HttpServletRequest request,String name){
        if (request == null || name == null){
            try {
                throw new IllegalAccessException("参数为空");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

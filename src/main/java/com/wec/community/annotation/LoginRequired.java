package com.wec.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//ElementType.METHOD注解作用于方法之上
@Retention(RetentionPolicy.RUNTIME)//程序运行时有效
public @interface LoginRequired {

}

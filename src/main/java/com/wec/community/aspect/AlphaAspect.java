package com.wec.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.wec.community.service.*.*(..) )")
    //第一个*代表所有的返回值，第二个*表示所以service类的所有方法 最后(..)表示所有参数
    public void ponintcut(){

    }

    @Before("ponintcut()")//表示开始时织入（）内为要生效的切点
    public void before(){
        System.out.println("before");
    }

    @After("ponintcut()")//表示最后时织入（）内为要生效的切点
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("ponintcut()")//表示有了返回值以后时织入（）内为要生效的切点
    public void afterReturning(){
        System.out.println("afterReturning");
    }
    @AfterThrowing("ponintcut()")//表示抛异常时织入（）内为要生效的切点
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    @Around("ponintcut()")//前后都织入逻辑
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{//参数ProceedingJoinPoint joinPoint表示生效的切点
        System.out.println("around before");//之前
        Object obj = joinPoint.proceed();//调用目标组件方法
        System.out.println("around after");//之后
        return obj;
    }

}

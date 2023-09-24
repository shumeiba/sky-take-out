package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 自定义切面
 */
@Aspect
@Component
@Slf4j
public class AutoFileAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..))" +
            " && @annotation(com.sky.annotation.AutoFile)")
    public void autoFilePointCut(){

    }
    //前置通知，需要传入接入点
    @Before("autoFilePointCut()")
    public void autoFile(JoinPoint joinPoint){
        log.info("开始进行公共字段填充");
    }
}

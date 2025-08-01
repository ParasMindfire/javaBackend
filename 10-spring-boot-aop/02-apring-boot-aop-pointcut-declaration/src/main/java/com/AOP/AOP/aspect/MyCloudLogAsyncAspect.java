package com.AOP.AOP.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class MyCloudLogAsyncAspect {
    @Before("com.AOP.AOP.aspect.PointCutExpressions.forDaoPackageNoGetterSetter()")   
    public void logToCloudAsync(){
        System.out.println("logging to cloud in async functions 1");
    }
}

package com.AOP.AOP.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class MyAPIanalyticsAspect {
    @Before("com.AOP.AOP.aspect.PointCutExpressions.forDaoPackageNoGetterSetter()")   
    public void performApiAnalytics(){
        System.out.println("Performing api analytics 2");
    }
}

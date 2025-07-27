package com.AOP.AOP.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class PointCutExpressions {
    @Pointcut("execution(* com.AOP.AOP.dao.*.*(..))")
    public void forDaoPackage(){}

    @Pointcut("execution(* com.AOP.AOP.dao.*.get*(..))")
    public void getter(){}

    @Pointcut("execution(* com.AOP.AOP.dao.*.set*(..))")
    public void setter(){}

    @Pointcut("forDaoPackage() && !(getter() || setter())")
    public void forDaoPackageNoGetterSetter(){}
}

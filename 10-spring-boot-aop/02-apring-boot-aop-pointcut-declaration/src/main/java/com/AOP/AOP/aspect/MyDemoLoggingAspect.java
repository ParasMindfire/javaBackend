package com.AOP.AOP.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyDemoLoggingAspect {

    @Before("execution(* com.AOP.AOP.dao.*.*(..))")//first star is for any return type , 2nd is for any class and 3rd is for any method
    // @Before("execution(* add*(..))")
    // @Before("execution(* add*(com.AOP.AOP.Account, ..))")
    // @Before("execution(* add*(com.AOP.AOP.Account))")
    // @Before("execution(* add*())")
    // @Before("execution(void add*())")
    // @Before("execution(public void add*())")
    // @Before("execution(public void com.AOP.AOP.dao.AccoutDAO.addAccount())")
    // @Before("execution(public void addAccount())")
    public void beforeAddAccountAdvice(){
        System.out.println("Exceuting before add account function");
    }
}

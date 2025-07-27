package com.AOP.AOP.aspect;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.AOP.AOP.Account;

@Aspect
@Component
@Order(3)
public class MyDemoLoggingAspect {

    @Around("execution(* com.AOP.AOP.service.TrafficFortuneService.getFortune(..))")
    public Object aroundGetFortune(ProceedingJoinPoint theProceedingJoinPoint)throws Throwable{
        String method=theProceedingJoinPoint.getSignature().toShortString();
        System.out.println("\n=======> Executing @After (finally) on method : "+method);

        long begin=System.currentTimeMillis();


        Object result=null;

        try {
            result=theProceedingJoinPoint.proceed();//this is the line responisble to throw error
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // result="major accident! But no worries , your private aop helicopter is on the way";
            throw e;
        }
        

        long end=System.currentTimeMillis();

        long duration = end-begin;
        System.out.println("\n=======>Duration "+ duration/1000.0 +" seconds");

        return result;
    }

    @After("execution(* com.AOP.AOP.dao.AccoutDAO.findAccount(..))")
    public void afterFinallyFindAccountsAdvice(JoinPoint theJoinPoint){
        String method=theJoinPoint.getSignature().toShortString();
        System.out.println("\n==========> Executing @After (finally) on method : "+method);
    }

    @AfterThrowing(pointcut="execution(* com.AOP.AOP.dao.AccoutDAO.findAccount(..))",throwing="theExc")
    public void afterThrowingFindAccountsAdvice(JoinPoint theJoinPoint,Throwable theExc){
        String method=theJoinPoint.getSignature().toShortString();
        System.out.println("\n==========> Executing @AfterThrowing on method : "+method);
        System.out.println("\n==========> the exception is  : "+theExc);
    }

    @AfterReturning(pointcut="execution(* com.AOP.AOP.dao.AccoutDAO.findAccount(..))",returning="result")
    public void afterReturningFindAccountsAdvice(JoinPoint theJoinPoint,List<Account>result){
        String method=theJoinPoint.getSignature().toShortString();
        System.out.println("\n==========> Executing @AfterRunning on method : "+method);
        System.out.println("\n========> Result is : "+result);

        convertAccountNamesToUpperCase(result);

        System.out.println("\n========> modified Result is : "+result);
    }

    private void convertAccountNamesToUpperCase(List<Account>result){
        for(Account acc:result){
            String theUpperCase=acc.getName().toUpperCase();
            acc.setName(theUpperCase);
        }
    }

    @Before("com.AOP.AOP.aspect.PointCutExpressions.forDaoPackage()")
    // @Before("execution(* com.AOP.AOP.dao.*.*(..))")//first star is for any return type , 2nd is for any class and 3rd is for any method
    // @Before("execution(* add*(..))")
    // @Before("execution(* add*(com.AOP.AOP.Account, ..))")
    // @Before("execution(* add*(com.AOP.AOP.Account))")
    // @Before("execution(* add*())")
    // @Before("execution(void add*())")
    // @Before("execution(public void add*())")
    // @Before("execution(public void com.AOP.AOP.dao.AccoutDAO.addAccount())")
    // @Before("execution(public void addAccount())")
    public void beforeAddAccountAdvice(JoinPoint theJointPoint) {
        System.out.println("Executing before add account function 3");
        MethodSignature methodSignature = (MethodSignature) theJointPoint.getSignature();
        Object [] args=theJointPoint.getArgs();
        System.out.println("Method " + methodSignature);
        for(Object a:args){
            System.out.println("argument is "+a);
            if(a instanceof Account){
                Account theAccount=(Account)a;
                System.out.println("Acocunt name is "+theAccount.getName());
                System.out.println("Acocunt level is "+theAccount.getLevel());
            }
        }
    }
}

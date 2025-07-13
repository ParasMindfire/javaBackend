package com.paras.springCore.common;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class CricketCoach implements Coach {

    public CricketCoach(){
        System.out.println("Constructor of CricketCoach");
    }

    //init method
    @PostConstruct
    public void doMyStartupStuffs(){
        System.out.println("In doMyStartupStuffs(): "+getClass().getSimpleName());
    }

    //destroy method
    @PreDestroy
    public void doMyCleanupStuffs(){
        System.out.println("In doMyCleanupStuffs(): "+getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Practise cricket fast  for 15mins !!!";
    }

}

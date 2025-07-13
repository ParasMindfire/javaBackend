package com.paras.springCore.common;

import org.springframework.stereotype.Component;

@Component
public class BaseballCoach implements Coach {

    public BaseballCoach(){
        System.out.println("Constructor of baseballCOach");
    }
    @Override
    public String getDailyWorkout() {
        return "Throw baseball at 150kmph";
    }

}

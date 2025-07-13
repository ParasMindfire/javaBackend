package com.paras.springCore.common;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class BaseballCoach implements Coach {

    public BaseballCoach(){
        System.out.println("Constructor of baseballCOach");
    }
    @Override
    public String getDailyWorkout() {
        return "Throw baseball at 150kmph";
    }

}

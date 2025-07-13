package com.paras.springCore.common;

import org.springframework.stereotype.Component;

@Component
public class BaseballCoach implements Coach {

    @Override
    public String getDailyWorkout() {
        return "Throw baseball at 150kmph";
    }

}

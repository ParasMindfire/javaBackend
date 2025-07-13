package com.paras.springCore.common;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class TrackCoach implements Coach {

    public TrackCoach(){
        System.out.println("Constructor of track coach");
    }

    @Override
    public String getDailyWorkout() {
        return "Run faster than Usain Bolt";
    }

}

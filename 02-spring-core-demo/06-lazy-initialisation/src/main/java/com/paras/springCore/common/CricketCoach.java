package com.paras.springCore.common;

import org.springframework.stereotype.Component;

@Component
public class CricketCoach implements Coach {

    public CricketCoach(){
        System.out.println("Constructor of CricketCoach");
    }

    @Override
    public String getDailyWorkout() {
        return "Practise cricket fast  for 15mins !!!";
    }

}

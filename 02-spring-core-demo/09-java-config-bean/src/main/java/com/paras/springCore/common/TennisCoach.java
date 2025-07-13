package com.paras.springCore.common;

import org.springframework.stereotype.Component;

@Component
public class TennisCoach implements Coach {

    public TennisCoach(){
        System.out.println("Constructor of TennisCoach");
    }

    @Override
    public String getDailyWorkout() {
        return "Practise tennis backheand";
    }

}

package com.paras.springCore.common;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TennisCoach implements Coach {

    public TennisCoach(){
        System.out.println("Constructor of TennisCoach");
    }

    @Override
    public String getDailyWorkout() {
        return "Practise tennis backheand";
    }

}

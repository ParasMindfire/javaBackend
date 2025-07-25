package com.paras.springCore.common;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CricketCoach implements Coach {

    public CricketCoach(){
        System.out.println("Constructor of CricketCoach");
    }

    @Override
    public String getDailyWorkout() {
        return "Practise cricket fast  for 15mins !!!";
    }

}

package com.paras.springCore.common;

public class SwimCoach implements Coach{

    public SwimCoach(){
        System.out.println("Inside the construcotr of "+getClass().getName());
    }

    @Override
    public String getDailyWorkout() {
        return "Swim 1000m as warmup ";
    }

}

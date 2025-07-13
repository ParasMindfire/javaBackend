package com.paras.springCore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paras.springCore.common.Coach;

@RestController
public class DemoController {
    private Coach myCoach;
    private Coach anotherCoach;

    // @Autowired
    // public void anyMethod(Coach theCoach){
    //     myCoach=theCoach;
    // }

    @Autowired
    public void setCoach(
        @Qualifier("cricketCoach") Coach theCoach,
        @Qualifier("cricketCoach") Coach theAnotherCoach){
        System.out.println("Inisde demo controller");
        myCoach=theCoach;
        anotherCoach=theAnotherCoach;
    }

    @GetMapping("/dialyworkout")
    public String getDailyWorkout(){
        return myCoach.getDailyWorkout();
    }

    @GetMapping("/check")
    public String getCheck(){
        return "check if both beans are mapping to same pointer :"+ (myCoach==anotherCoach);
    }

}

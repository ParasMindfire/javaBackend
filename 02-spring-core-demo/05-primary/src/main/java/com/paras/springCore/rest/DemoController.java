package com.paras.springCore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paras.springCore.common.Coach;

@RestController
public class DemoController {
    private Coach myCoach;

    // @Autowired
    // public void anyMethod(Coach theCoach){
    //     myCoach=theCoach;
    // }

    @Autowired
    public void setCoach(Coach theCoach){
        myCoach=theCoach;
    }

    @GetMapping("/dialyworkout")
    public String getDailyWorkout(){
        return myCoach.getDailyWorkout();
    }

}

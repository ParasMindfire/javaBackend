package com.paras.springCore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    private Coach myCoach;

    @Autowired
    public DemoController(Coach theCoach){
        myCoach=theCoach;
    }
    
    @GetMapping("/dialyworkout")
    public String getDailyWorkout(){
        return myCoach.getDailyWorkout();
    }

}

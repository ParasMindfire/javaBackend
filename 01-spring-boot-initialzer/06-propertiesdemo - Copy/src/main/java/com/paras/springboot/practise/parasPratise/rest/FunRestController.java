package com.paras.springboot.practise.parasPratise.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunRestController {

    @Value("${team.name}")
    private String teamName;
     @Value("${coach.name}")
    private String coachName;

    @GetMapping("/team")
        public String teamInfo(){
            return "TeamName is "+teamName;
    }

    @GetMapping("/coach")
        public String coachInfo(){
            return "coachName is "+coachName;
    }

    @GetMapping("/")
    public String sayHello(){
        return "Hello";
    }

     @GetMapping("/ok")
    public String sayHello1(){
        return "Hello worlddd";
    }
}

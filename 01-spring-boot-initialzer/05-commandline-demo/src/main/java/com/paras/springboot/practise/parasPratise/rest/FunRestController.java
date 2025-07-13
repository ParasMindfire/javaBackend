package com.paras.springboot.practise.parasPratise.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunRestController {
    @GetMapping("/")
    public String sayHello(){
        return "Hello";
    }

     @GetMapping("/ok")
    public String sayHello1(){
        return "Hello worlddd";
    }
}

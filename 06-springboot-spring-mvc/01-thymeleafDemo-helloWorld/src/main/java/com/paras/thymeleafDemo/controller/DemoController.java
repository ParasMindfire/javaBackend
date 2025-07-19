package com.paras.thymeleafDemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;




@Controller
public class DemoController {

    @GetMapping("/hello")
    public String sayHello(Model theModel){
        theModel.addAttribute("theData",java.time.LocalDateTime.now());
        return "helloworld";//thymeleaf will look for helloworld.html
    }

}

package com.paras.thymeleafDemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller 
public class HelloWorldController {

    @RequestMapping("/showForm")
    public String showForm(){
        return "helloworld-form";
    }


    @RequestMapping("/processForm")
    public String processForm(){
        return "helloworld";
    }


    @RequestMapping("/processFormVersionTwo")
    public String letsShoutDude(HttpServletRequest request,Model model){
        String theName=request.getParameter("studentName");
        theName=theName.toUpperCase();
        String result="Yo ! "+theName;
        model.addAttribute("message",result);


        return "helloworld";
    }

    @PostMapping("/processFormVersionThree")
    public String processFormVersionThree(@RequestParam("studentName") String theName,Model model){
        theName=theName.toUpperCase();
        String result="Yo ! "+theName;
        model.addAttribute("message",result);


        return "helloworld";
    }
}

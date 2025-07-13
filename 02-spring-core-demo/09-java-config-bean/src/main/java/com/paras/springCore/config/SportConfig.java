package com.paras.springCore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paras.springCore.common.Coach;
import com.paras.springCore.common.CricketCoach;
import com.paras.springCore.common.SwimCoach;

@Configuration
public class SportConfig {
    @Bean("aquatic")
    public Coach swimCoach(){
        return new SwimCoach();
    }
}

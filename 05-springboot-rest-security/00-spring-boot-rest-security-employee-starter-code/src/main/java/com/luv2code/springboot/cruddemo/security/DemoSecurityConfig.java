package com.luv2code.springboot.cruddemo.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        JdbcUserDetailsManager jdbcUserDetailsManager=new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select user_id,pw,active from members where user_id=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select user_id,role from roles where user_id=?");
        return jdbcUserDetailsManager;
    } 


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer->
            configurer  
                .requestMatchers(HttpMethod.GET,"/api/employees").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,"/api/employees/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.POST,"/api/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,"/api/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE,"/api/employees").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,"/api/employees/**").hasRole("MANAGER")
        );


        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf->csrf.disable());

        return http.build();
    }





    // public InMemoryUserDetailsManager userDetailsManager(){
    //     UserDetails paras= User.builder()
    //     .username("paras")
    //     .password("{noop}test123")
    //     .roles("EMPLOYEE","MANAGER","ADMIN").build();

    //     UserDetails abhijeet= User.builder()
    //     .username("abhijeet")
    //     .password("{noop}test123")
    //     .roles("EMPLOYEE","ADMIN").build();

    //     UserDetails subham= User.builder()
    //     .username("subham")
    //     .password("{noop}test123")
    //     .roles("EMPLOYEE").build();


    //     return new InMemoryUserDetailsManager(paras,abhijeet,subham);
    // }

}

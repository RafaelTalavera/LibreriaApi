package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.app.auth.handler.LoginSuccessHandler;



@Configuration
public class SpringSecurityConfig {
   
    @Autowired
    private LoginSuccessHandler successHandler;



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


 
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
    return http
            .authorizeHttpRequests()
            .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar-libro", "/locale")
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .successHandler(successHandler)
            .loginPage("/login")
            .permitAll()
            .and()
            .logout().permitAll()
            .and()
            .exceptionHandling().accessDeniedPage("/error_404")
            .and().build();
    }
}
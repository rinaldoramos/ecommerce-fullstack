package com.ecommerce.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class GeneralBeans {

    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }
}

package com.ecommerce;

import com.ecommerce.security.utils.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SbEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbEcommerceApplication.class, args);
    }

}

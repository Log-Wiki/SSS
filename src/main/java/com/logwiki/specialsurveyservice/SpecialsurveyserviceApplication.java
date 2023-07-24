package com.logwiki.specialsurveyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpecialsurveyserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpecialsurveyserviceApplication.class, args);
    }

}

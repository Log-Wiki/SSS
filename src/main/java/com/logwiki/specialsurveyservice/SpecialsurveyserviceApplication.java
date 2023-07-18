package com.logwiki.specialsurveyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpecialsurveyserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpecialsurveyserviceApplication.class, args);
	}

}

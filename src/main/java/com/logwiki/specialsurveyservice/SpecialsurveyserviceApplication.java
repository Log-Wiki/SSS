package com.logwiki.specialsurveyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpecialsurveyserviceApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpecialsurveyserviceApplication.class, args);
  }

}

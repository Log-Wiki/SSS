package com.logwiki.specialsurveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logwiki.specialsurveyservice.api.controller.account.AccountController;
import com.logwiki.specialsurveyservice.api.service.account.SignupAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    AccountController.class,
})
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected AuthenticationManagerBuilder authenticationManagerBuilder;

  @MockBean
  protected SignupAccountService signupAccountService;
}

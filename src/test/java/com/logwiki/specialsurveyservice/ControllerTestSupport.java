package com.logwiki.specialsurveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logwiki.specialsurveyservice.api.controller.account.AccountController;
import com.logwiki.specialsurveyservice.api.controller.auth.AuthController;
import com.logwiki.specialsurveyservice.api.controller.giveaway.GiveawayController;
import com.logwiki.specialsurveyservice.api.controller.userdetail.UserDetailController;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.auth.AuthService;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.userdetail.UserDetailService;
import com.logwiki.specialsurveyservice.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        AccountController.class,
        AuthController.class,
        UserDetailController.class,
        GiveawayController.class
})
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected AuthenticationManagerBuilder authenticationManagerBuilder;

  @MockBean
  protected AccountService accountService;

  @MockBean
  protected TokenProvider tokenProvider;

  @MockBean
  protected AuthService authService;

  @MockBean
  protected UserDetailService userDetailService;

  @MockBean
  protected GiveawayService giveawayService;
}

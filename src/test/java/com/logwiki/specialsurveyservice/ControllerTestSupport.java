package com.logwiki.specialsurveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logwiki.specialsurveyservice.api.controller.account.AccountController;
import com.logwiki.specialsurveyservice.api.controller.auth.AuthController;
import com.logwiki.specialsurveyservice.api.controller.giveaway.GiveawayController;
import com.logwiki.specialsurveyservice.api.controller.orders.OrderController;
import com.logwiki.specialsurveyservice.api.controller.payment.PaymentController;
import com.logwiki.specialsurveyservice.api.controller.question.QuestionController;
import com.logwiki.specialsurveyservice.api.controller.surveyresult.SurveyResultController;
import com.logwiki.specialsurveyservice.api.controller.userdetail.UserDetailController;
import com.logwiki.specialsurveyservice.api.service.account.AccountService;
import com.logwiki.specialsurveyservice.api.service.auth.AuthService;
import com.logwiki.specialsurveyservice.api.service.giveaway.GiveawayService;
import com.logwiki.specialsurveyservice.api.service.order.RegistOrderService;
import com.logwiki.specialsurveyservice.api.service.payment.AuthenticationPaymentService;
import com.logwiki.specialsurveyservice.api.service.question.QuestionAnswerService;
import com.logwiki.specialsurveyservice.api.service.question.QuestionService;
import com.logwiki.specialsurveyservice.api.service.surveyresult.SurveyResultService;
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
        GiveawayController.class,
        QuestionController.class,
        SurveyResultController.class,
        QuestionController.class,
        OrderController.class,
        PaymentController.class
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

    @MockBean
    protected QuestionAnswerService questionAnswerService;
    
    @MockBean
    protected QuestionService questionService;

    @MockBean
    protected SurveyResultService surveyResultService;

    @MockBean
    protected RegistOrderService registOrderService;

    @MockBean
    protected AuthenticationPaymentService authenticationPaymentService;
}

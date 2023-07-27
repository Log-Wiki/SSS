package com.logwiki.specialsurveyservice.api.service.sse;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.controller.question.request.MultipleChoiceCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionCreateRequest;
import com.logwiki.specialsurveyservice.domain.SseEmiters.EmitterRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.event.spi.EventSource;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Transactional
class SseConnectServiceTest extends IntegrationTestSupport {

}
package com.logwiki.specialsurveyservice.api.service.question;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.logwiki.specialsurveyservice.api.controller.question.request.MultipleChoiceCreateRequest;
import com.logwiki.specialsurveyservice.api.controller.question.request.QuestionCreateRequest;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionModifyServiceRequest;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;

    @DisplayName("객관식 문항은 보기를 가지지 않을경우 에러를 반환")
    @Test
    void multipleChoiceNeedMultipleChoice() {
        // given
        List<MultipleChoiceCreateRequest> list = new ArrayList<>();
        list.add(MultipleChoiceCreateRequest
                .builder()
                .linkNumber(1L)
                .content("내용1")
                .build());

        Assertions.assertThrows(BaseException.class, () -> {
            questionService.addQuestion(QuestionCreateRequest.builder()
                    .questionNumber(1L)
                    .content("문항1")
                    .type(QuestionCategoryType.MULTIPLE_CHOICE)
                    .imgAddress(null)
                    .multipleChoices(null)
                    .build().toServiceRequest());
        });
    }

    @DisplayName("주관식 문항은 보기를 가질 경우 에러를 반환")
    @Test
    void shorFormCantHaveMultipleChoice() {
        // given
        List<MultipleChoiceCreateRequest> list = new ArrayList<>();
        list.add(MultipleChoiceCreateRequest
                .builder()
                .linkNumber(1L)
                .content("내용1")
                .build());

        Assertions.assertThrows(BaseException.class, () -> {
            questionService.addQuestion(QuestionCreateRequest.builder()
                    .questionNumber(1L)
                    .content("문항1")
                    .type(QuestionCategoryType.SHORT_FORM)
                    .imgAddress(null)
                    .multipleChoices(list)
                    .build()
                    .toServiceRequest());
        });
    }

    @DisplayName("문항을 찾지 못할 경우 에러를 반환")
    @Test
    void notFoundQuestionThrowError() {
        // given
        QuestionModifyServiceRequest dto = new QuestionModifyServiceRequest();

        // when
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(null));

        // then
        Assertions.assertThrows(BaseException.class, () -> {
            questionService.modifyQuestion(dto);
        });
    }

}
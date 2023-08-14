package com.logwiki.specialsurveyservice.api.service.question;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.question.request.QuestionModifyServiceRequest;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.domain.questioncategory.QuestionCategoryType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class QuestionServiceTest extends IntegrationTestSupport {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;

    @DisplayName("문항을 찾지 못할 경우 에러를 반환한다.")
    @Test
    void notFoundQuestionThrowError() {
        // given
        Long invalidId = 1L;
        QuestionModifyServiceRequest invalidQuestionModifyServiceRequest = QuestionModifyServiceRequest
                .builder()
                .id(invalidId)
                .build();

        // when // then
        Assertions.assertThatThrownBy(() -> questionService.modifyQuestion(invalidQuestionModifyServiceRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("문항을 찾을 수 없습니다.");
    }

    @DisplayName("질문의 내용, 이미지 주소, 타입을 할 수 있다.")
    @Test
    void modifyQuestion() {
        // given
        MultipleChoice multipleChoice = MultipleChoice
                .builder()
                .content("다중 선택 내용")
                .linkNumber(0L)
                .build();

        Question question = Question
                .builder()
                .questionNumber(1L)
                .title("질문 title입니다.")
                .content("질문 content입니다.")
                .imgAddress("tempAddress")
                .type(QuestionCategoryType.MULTIPLE_CHOICE)
                .essential(true)
                .multipleChoice(List.of(multipleChoice))
                .build();

        Question savedQuestion = questionRepository.save(question);
        Long questionId = savedQuestion.getId();

        String updateContent = "새로운 내용";
        String updateImgAddress = "newImgAddress";
        QuestionCategoryType updateQuestionCategoryType = QuestionCategoryType.CHECK_BOX;
        QuestionModifyServiceRequest questionModifyServiceRequest = QuestionModifyServiceRequest
                .builder()
                .id(questionId)
                .content(updateContent)
                .imgAddress(updateImgAddress)
                .type(updateQuestionCategoryType)
                .build();

        // when
        questionService.modifyQuestion(questionModifyServiceRequest);

        // then
        Question updatedQuestion = questionRepository.findById(questionId).get();
        assertThat(updatedQuestion.getContent()).isEqualTo(updateContent);
        assertThat(updatedQuestion.getImgAddress()).isEqualTo(updateImgAddress);
        assertThat(updatedQuestion.getType()).isEqualTo(updateQuestionCategoryType);

    }

}
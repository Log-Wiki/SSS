package com.logwiki.specialsurveyservice.api.service.question;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.question.response.MultipleChoiceResponse;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoiceRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MultipleChoiceServiceTest extends IntegrationTestSupport {

    @Autowired
    private MultipleChoiceService multipleChoiceService;
    @Autowired
    private MultipleChoiceRepository multipleChoiceRepository;

    @DisplayName("다중선택ID를 이용하여 다중선택을 조회한다.")
    @Test
    void findMultipleChoice() {
        // given
        String multipleChoiceContent = "다중선택 내용";
        Long multipleChoiceLinkNumber = 1L;
        MultipleChoice multipleChoice = MultipleChoice
                .builder()
                .content(multipleChoiceContent)
                .linkNumber(multipleChoiceLinkNumber)
                .build();

        MultipleChoice saveMultipleChoice = multipleChoiceRepository.save(multipleChoice);

        // when
        MultipleChoiceResponse multipleChoiceResponse = multipleChoiceService.findMultipleChoice(saveMultipleChoice.getId());

        // then
        assertThat(multipleChoiceResponse.getContent()).isEqualTo(multipleChoiceContent);
        assertThat(multipleChoiceResponse.getLinkNumber()).isEqualTo(multipleChoiceLinkNumber);
    }

    @DisplayName("문항 보기를 찾지 못할시 에러 반환")
    @Test
    void notFindMultipleChoiceThrowError() {
        // given
        Long invalidMultipleChoiceId = 1L;

        // when // then
        Assertions.assertThatThrownBy(() -> multipleChoiceService.findMultipleChoice(invalidMultipleChoiceId))
                .isInstanceOf(BaseException.class)
                .hasMessage("문항 보기 정보를 찾을 수 없습니다.");
    }
}
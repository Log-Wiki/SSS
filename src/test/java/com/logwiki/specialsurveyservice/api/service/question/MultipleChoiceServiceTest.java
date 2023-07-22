package com.logwiki.specialsurveyservice.api.service.question;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoiceRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MultipleChoiceServiceTest {

  @InjectMocks
  private MultipleChoiceService multipleChoiceService;

  @Mock
  private MultipleChoiceRepository multipleChoiceRepository;

  @DisplayName("문항 보기를 찾지 못할시 에러 반환")
  @Test
  void notFindMultipleChoiceThrowError() {
    // given
    MultipleChoice multipleChoice = null;
    Long id = 1L;

    // when
    when(multipleChoiceRepository.findById(any())).thenReturn(Optional.ofNullable(multipleChoice));

    // then
    Assertions.assertThrows(BaseException.class, () -> {
      multipleChoiceService.findMultipleChoice(id);
    });
  }
}
package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.question.response.MultipleChoiceResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiError;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoiceRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MultipleChoiceService {

  private final MultipleChoiceRepository multipleChoiceRepository;

  public MultipleChoiceResponse findMultipleChoice(Long id) {
    MultipleChoice multipleChoice = multipleChoiceRepository.findById(id)
        .orElseThrow(() -> new BaseException(new ApiError("문항 보기 정보를 찾을 수 없습니다.", 2000)));
    return MultipleChoiceResponse.from(multipleChoice);
  }

}

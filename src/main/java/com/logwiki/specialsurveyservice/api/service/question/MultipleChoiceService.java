package com.logwiki.specialsurveyservice.api.service.question;


import com.logwiki.specialsurveyservice.api.service.question.response.MultipleChoiceResponse;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoiceRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MultipleChoiceService {

    private final MultipleChoiceRepository multipleChoiceRepository;

    @Transactional
    public MultipleChoiceResponse findMultipleChoice(Long id) {
        MultipleChoice multipleChoice = multipleChoiceRepository.findById(id)
                .orElseThrow(() -> new BaseException("문항 보기 정보를 찾을 수 없습니다.", 2000));
        return MultipleChoiceResponse.from(multipleChoice);
    }

}

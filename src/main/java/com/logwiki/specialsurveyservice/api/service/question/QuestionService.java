package com.logwiki.specialsurveyservice.api.service.question;

import com.logwiki.specialsurveyservice.api.service.question.request.QuestionModifyServiceRequest;
import com.logwiki.specialsurveyservice.domain.question.Question;
import com.logwiki.specialsurveyservice.domain.question.QuestionRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public void modifyQuestion(QuestionModifyServiceRequest dto) {
        Question question = questionRepository.findById(dto.getId())
                .orElseThrow(() -> new BaseException("문항을 찾을 수 없습니다.", 3000));
        question.updateQuestion(dto);
    }
}

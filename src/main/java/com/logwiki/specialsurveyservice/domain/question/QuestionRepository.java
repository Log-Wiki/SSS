package com.logwiki.specialsurveyservice.domain.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<List<Question>> findBySurveyId(Long surveyId);
}

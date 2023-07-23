package com.logwiki.specialsurveyservice.domain.questioncategory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {

    QuestionCategory findQuestionCategoryByType(QuestionCategoryType questionCategoryType);

}

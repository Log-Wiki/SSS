package com.logwiki.specialsurveyservice.domain.surveycategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyCategoryRepository extends JpaRepository<SurveyCategory, Long> {

    SurveyCategory findSurveyCategoryByType(SurveyCategoryType surveyCategoryType);
}

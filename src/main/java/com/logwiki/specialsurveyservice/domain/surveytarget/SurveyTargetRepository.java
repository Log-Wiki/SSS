package com.logwiki.specialsurveyservice.domain.surveytarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyTargetRepository extends JpaRepository<SurveyTarget, Long> {

}

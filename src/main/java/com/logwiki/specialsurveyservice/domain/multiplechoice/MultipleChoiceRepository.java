package com.logwiki.specialsurveyservice.domain.multiplechoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultipleChoiceRepository extends JpaRepository<MultipleChoice, Long> {

}

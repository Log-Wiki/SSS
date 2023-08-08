package com.logwiki.specialsurveyservice.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findScheduleByJobGroupAndJobName(String jobGroup, String jobName);

    List<Schedule> findScheduleBySurveyId(Long surveyId);

    List<Schedule> findScheduleByRun(ScheduleRunType run);

}

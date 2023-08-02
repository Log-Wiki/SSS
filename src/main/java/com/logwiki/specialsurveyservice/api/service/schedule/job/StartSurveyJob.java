package com.logwiki.specialsurveyservice.api.service.schedule.job;

import com.logwiki.specialsurveyservice.domain.schedule.Schedule;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRepository;
import com.logwiki.specialsurveyservice.domain.survey.Survey;
import com.logwiki.specialsurveyservice.domain.survey.SurveyRepository;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

@AllArgsConstructor
public class StartSurveyJob implements Job {

    private final SurveyRepository surveyRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Long surveyId = jobDataMap.getLong("surveyId");

        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() ->
                new BaseException("없는 설문입니다.", 3005));
        survey.toOpen();

        JobKey jobKey = context.getJobDetail().getKey();
        Schedule schedule = scheduleRepository.findScheduleByJobGroupAndJobName(jobKey.getGroup(), jobKey.getName())
                .orElseThrow();
        schedule.endSchedule();

    }
}

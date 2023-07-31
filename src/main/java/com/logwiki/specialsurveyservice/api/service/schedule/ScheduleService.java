package com.logwiki.specialsurveyservice.api.service.schedule;

import com.logwiki.specialsurveyservice.api.controller.schedule.request.ScheduleCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.job.StartSurveyJob;
import com.logwiki.specialsurveyservice.domain.schedule.Schedule;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRepository;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRunType;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final Scheduler scheduler;
    private final ScheduleRepository scheduleRepository;
    private static final String START_SURVEY = "start-survey";
    private static final String END_SURVEY = "end-survey";

    @Transactional
    public String addStartSurveySchedule(ScheduleCreateRequest dto) throws SchedulerException {
        JobDetail jobDetail = buildJobDetailByDto(dto, START_SURVEY);

        JobKey jobKey = jobDetail.getKey();
        String name = jobKey.getName();
        String group = jobKey.getGroup();

        Date date = Date.from(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant());

        Trigger trigger = buildOneTimeJobTrigger(jobDetail, date, START_SURVEY);
        scheduler.scheduleJob(jobDetail, trigger);
        Schedule schedule = Schedule.builder()
                .type(ScheduleType.START_SURVEY)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(dto.getStartTime())
                .surveyId(dto.getSurveyId())
                .jobGroup(group)
                .jobName(name).build();

        scheduleRepository.save(schedule);
        return "";
    }

    // 요청으로 생성하기
    private JobDetail buildJobDetailByDto(ScheduleCreateRequest dto, String groupName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("surveyId", dto.getSurveyId());
        return JobBuilder.newJob(StartSurveyJob.class)
                .withIdentity(UUID.randomUUID().toString(), groupName)
                .withDescription("생성된 JOB" + String.valueOf(dto.getSurveyId()))
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildOneTimeJobTrigger(JobDetail jobDetail, Date date, String groupName) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), groupName)
                .startAt(date)
                .build();
    }

    // 작업중인 스케줄러를 출력
    public void getCurrentlyExecutingJobs() throws SchedulerException {
        List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext jobContext : currentJobs) {
            JobKey jobKey = jobContext.getJobDetail().getKey();
            System.out.println(
                    "Job is running: " + jobKey.getName() + ", Group: " + jobKey.getGroup());
        }
    }

    // 대기중인 job 출력
    public void printAllJobs() throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            System.out.println(groupName);
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                System.out.println(jobKey.toString());
                System.out.println(
                        "Job name: " + jobKey.getName() + ", Group: " + jobKey.getGroup());
            }
        }
    }


    // Job 삭제
    public void deleteJob(String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new BaseException("해당 스케줄링이 없습니다.", 9999);
        }
    }
}

package com.logwiki.specialsurveyservice.api.service.schedule;

import com.logwiki.specialsurveyservice.api.controller.schedule.request.ScheduleCreateRequest;
import com.logwiki.specialsurveyservice.api.service.schedule.job.EndSurveyJob;
import com.logwiki.specialsurveyservice.api.service.schedule.job.StartSurveyJob;
import com.logwiki.specialsurveyservice.api.service.schedule.response.ScheduleResponse;
import com.logwiki.specialsurveyservice.domain.schedule.Schedule;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRepository;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleRunType;
import com.logwiki.specialsurveyservice.domain.schedule.ScheduleType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final Scheduler scheduler;

    private final ScheduleRepository scheduleRepository;
    private static final String START_SURVEY = "start-survey";
    private static final String END_SURVEY = "end-survey";

    @Transactional
    public void addSurveySchedule(Long surveyId, LocalDateTime startTime, LocalDateTime endTime) throws SchedulerException {
        ScheduleCreateRequest startSurveySchedule = ScheduleCreateRequest.builder()
                .surveyId(surveyId)
                .startTime(startTime)
                .build();
        ScheduleCreateRequest endSurveySchedule = ScheduleCreateRequest.builder()
                .surveyId(surveyId)
                .startTime(endTime)
                .build();
        addStartSurveySchedule(startSurveySchedule);
        addEndSurveySchedule(endSurveySchedule);
    }

    @Transactional
    public ScheduleResponse addStartSurveySchedule(ScheduleCreateRequest dto) throws SchedulerException {
        JobDetail jobDetail = buildJobDetailStartSurvey(dto, START_SURVEY);

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
        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public ScheduleResponse addEndSurveySchedule(ScheduleCreateRequest dto) throws SchedulerException {
        JobDetail jobDetail = buildJobDetailEndSurvey(dto, END_SURVEY);

        JobKey jobKey = jobDetail.getKey();
        String name = jobKey.getName();
        String group = jobKey.getGroup();

        Date date = Date.from(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant());

        Trigger trigger = buildOneTimeJobTrigger(jobDetail, date, END_SURVEY);
        scheduler.scheduleJob(jobDetail, trigger);
        Schedule schedule = Schedule.builder()
                .type(ScheduleType.END_SURVEY)
                .run(ScheduleRunType.BEFORE_RUN)
                .startTime(dto.getStartTime())
                .surveyId(dto.getSurveyId())
                .jobGroup(group)
                .jobName(name).build();

        scheduleRepository.save(schedule);
        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public List<ScheduleResponse> getBeforeRunSchedule() throws SchedulerException {
        return scheduleRepository.findScheduleByRun(ScheduleRunType.BEFORE_RUN)
                .orElseThrow(() -> new BaseException("동작전 스케줄러가 없습니다.", 6001))
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ScheduleResponse> getSchedulesBySurveyId(Long surveyId) {
        return scheduleRepository.findScheduleBySurveyId(surveyId)
                .orElseThrow(() -> new BaseException("등록된 스케줄러가 없습니다.", 6000))
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    // 설문 시작 JobDetail 작성
    private JobDetail buildJobDetailStartSurvey(ScheduleCreateRequest dto, String groupName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("surveyId", dto.getSurveyId());
        return JobBuilder.newJob(StartSurveyJob.class)
                .withIdentity(UUID.randomUUID().toString(), groupName)
                .withDescription("생성된 JOB" + String.valueOf(dto.getSurveyId()))
                .usingJobData(jobDataMap)
                .build();
    }

    // 설문 끝 JobDetail 작성
    private JobDetail buildJobDetailEndSurvey(ScheduleCreateRequest dto, String groupName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("surveyId", dto.getSurveyId());
        return JobBuilder.newJob(EndSurveyJob.class)
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

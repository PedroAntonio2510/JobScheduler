package io.github.job.scheduler.service;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.entity.LogsJob;
import io.github.job.scheduler.entity.dto.JobDTO;
import io.github.job.scheduler.entity.dto.JobResponseDTO;
import io.github.job.scheduler.entity.enums.STATUS;
import io.github.job.scheduler.mapper.JobMapper;
import io.github.job.scheduler.repository.JobRepository;
import io.github.job.scheduler.utils.CronUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private LogsJobService logsJobService;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EmailNotificationService emailNotificationService;


    private final Map<Long, ScheduledFuture<?>> jobsActives = new HashMap<>();

    @PostConstruct
    public void initiateJobs() {
        List<Job> jobs = jobRepository.findAllByActivateTrue();
        jobs.forEach(this::scheduleJob);
    }

    public Job createJob(JobDTO data) {
        validate(data);
        Job newJob = jobMapper.toJob(data);
        newJob.setActivate(true);

        Job jobSaved = this.jobRepository.save(newJob);
        rabbitTemplate.convertAndSend("jobs.exchange", "jobs-pending", newJob);

        return jobSaved;
    }

    public Job updateJob(Job job) {
        validate(job);
        rabbitTemplate.convertAndSend("jobs.exchange", "jobs-pending", job);
        return this.jobRepository.save(job);
    }

    public void stopJob(Long jobId) {
        if (jobsActives.containsKey(jobId)) {
            jobsActives.get(jobId).cancel(false);
            jobsActives.remove(jobId);
        }
        jobRepository.deleteById(jobId);
        emailNotificationService.notificateSES("Your job with the id was deleted ", jobId);
    }

    public void scheduleJob(Job job) {
        if (jobsActives.containsKey(job.getId())) {
            jobsActives.get(job.getId()).cancel(false);
        }

        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> executarJob(job),
                new CronTrigger(job.getCronExpression()));
        jobsActives.put(job.getId(), future);
    }

    public void executarJob(Job job) {

        Job jobFound = jobRepository.findById(job.getId())
                .orElseThrow(() -> new EntityNotFoundException("Job not found!"));

        LogsJob jobLog = new LogsJob();
        jobLog.setJob(jobFound);
        jobLog.setExecutionDate(CronUtils.getCronDate(jobFound.getCronExpression()));

        try {
            emailNotificationService.notificateJobScheduledSES(job);
            jobLog.setStatus(STATUS.SUCESS);
        } catch (Exception e) {
            jobLog.setStatus(STATUS.ERROR);
            jobLog.setErrorMessage(e.getMessage());
        } finally {
            logsJobService.createLog(job.getId(), jobLog.getErrorMessage());
        }
    }

    public Page<JobResponseDTO> getAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Job> jobsPage = jobRepository.findAll(pageable);
        return jobsPage.map(jobMapper::toJobResponseDTO);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }

    public void validate(JobDTO data) {
        if (!CronUtils.isValidCronExpression(data.cronExpression())) {
            throw new IllegalArgumentException("Invalid cron expression");
        }
    }

    public void validate(Job job) {
        if (!CronUtils.isValidCronExpression(job.getCronExpression())) {
            throw new IllegalArgumentException("Invalid cron expression");
        }
    }

}

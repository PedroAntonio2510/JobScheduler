package io.github.job.scheduler.service;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.entity.LogsJob;
import io.github.job.scheduler.entity.dto.JobDTO;
import io.github.job.scheduler.entity.enums.STATUS;
import io.github.job.scheduler.mapper.JobMapper;
import io.github.job.scheduler.repository.JobRepository;
import io.github.job.scheduler.utils.CronUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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


    private final JobRepository jobRepository;
    private final LogsJobService logsJobService;
    private final JobMapper jobMapper;
    private final TaskScheduler taskScheduler;
    private final RabbitTemplate rabbitTemplate;
    private final EmailNotificationService emailNotificationService;

    public JobService(JobRepository jobRepository,
                      JobMapper jobMapper,
                      TaskScheduler taskScheduler,
                      RabbitTemplate rabbitTemplate,
                      EmailNotificationService emailNotificationService,
                      LogsJobService logsJobService) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.taskScheduler = taskScheduler;
        this.rabbitTemplate = rabbitTemplate;
        this.emailNotificationService = emailNotificationService;
        this.logsJobService = logsJobService;
    }

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

        rabbitTemplate.convertAndSend("jobs.exchange", "jobs-pending", newJob);

        return this.jobRepository.save(newJob);
    }

    public Job updateJob(Job job) {
        validate(job);
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

        LogsJob log = new LogsJob();
        log.setJob(job);
        log.setExecutionDate(CronUtils.getCronDate(job.getCronExpression()));

        try {
            // Lógica de execução do job
            rabbitTemplate.convertAndSend("jobs.exchange", "jobs-complete", job);
            log.setStatus(STATUS.SUCESS);
        } catch (Exception e) {
            log.setStatus(STATUS.ERROR);
            log.setErrorMessage(e.getMessage());
        } finally {
            logsJobService.createLog(log);
        }
        emailNotificationService.notificateSES();
        rabbitTemplate.convertAndSend("jobs.exchange", "jobs-complete", job);
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

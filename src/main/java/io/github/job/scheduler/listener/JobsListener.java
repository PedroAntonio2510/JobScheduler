package io.github.job.scheduler.listener;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.service.EmailNotificationService;
import io.github.job.scheduler.service.JobService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobsListener {

    private final EmailNotificationService emailNotificationService;
    private final JobService jobService;
    private final RabbitTemplate rabbitTemplate;

    public JobsListener(EmailNotificationService emailNotificationService,
                        JobService jobService, RabbitTemplate rabbitTemplate) {
        this.emailNotificationService = emailNotificationService;
        this.jobService = jobService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "jobs-pending.queue")
    public void processJob(Job job) {
        emailNotificationService.notificateSES(job);
        rabbitTemplate.convertAndSend("jobs.exchange", "jobs-schedule", job);
    }

    @RabbitListener(queues = "jobs-schedule.queue")
    public void scheduledJobs(Job job) {
        if (job.isActivate()) {
            jobService.scheduleJob(job);
        } else {
            rabbitTemplate.convertAndSend("jobs.exchange", "jobs-complete");
        }
    }

}

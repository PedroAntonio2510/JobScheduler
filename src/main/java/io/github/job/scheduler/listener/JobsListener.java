package io.github.job.scheduler.listener;

import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.service.EmailNotificationService;
import io.github.job.scheduler.service.JobService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobsListener {

    private final RabbitTemplate rabbitTemplate;
    private final EmailNotificationService emailNotificationService;
    private final JobService jobService;

    public JobsListener(RabbitTemplate rabbitTemplate,
                        EmailNotificationService emailNotificationService,
                        JobService jobService) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailNotificationService = emailNotificationService;
        this.jobService = jobService;
    }

    @RabbitListener(queues = "jobs-pending.queue")
    public void processJob(Job job) {
        jobService.scheduleJob(job);
        emailNotificationService.notificateSES(job);
    }

}

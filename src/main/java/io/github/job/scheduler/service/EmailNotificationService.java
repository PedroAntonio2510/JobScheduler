package io.github.job.scheduler.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import io.github.job.scheduler.entity.Job;
import io.github.job.scheduler.utils.CronUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
public class EmailNotificationService {

    @Autowired
    private AmazonSimpleEmailService amazonSES;

    @Autowired
    private SpringTemplateEngine templateEngine;

    static final String TEXTBODY = "Your order was realized, stay alert on email for updates. ";
    static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";


    public void notificateSES(Job job) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses("pedrinhodeccache@gmail.com"))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData("Your job with the name was scheduled: " + job.getName() + " with the following description " + CronUtils.getCronDate(job.getCronExpression())))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)))
                .withSource("antonio.pedro25@outlook.com");
        amazonSES.sendEmail(request);
    }

    public void notificateJobScheduledSES(Job job) {
        Context context = new Context();
        context.setVariable("jobName", job.getName());

        String content = templateEngine.process("email-reminder", context);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses("pedrinhodeccache@gmail.com"))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(content)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)))
                .withSource("antonio.pedro25@outlook.com");
        amazonSES.sendEmail(request);
    }

    public void notificateSES(String message, Long id) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses("pedrinhodeccache@gmail.com"))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(message + id))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)))
                .withSource("antonio.pedro25@outlook.com");
        amazonSES.sendEmail(request);
    }



}

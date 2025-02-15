package io.github.job.scheduler.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import io.github.job.scheduler.entity.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmailNotificationService {

    @Autowired
    private AmazonSimpleEmailService amazonSES;

    static final String TEXTBODY = "Your order was realized, stay alert on email for updates. ";
    static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";


    public void notificateSES(Job job) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses("pedrinhodeccache@gmail.com"))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData("You job with the name was scheduled: " + job.getName()))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)))
                .withSource("antonio.pedro25@outlook.com");
        amazonSES.sendEmail(request);
    }

    public void notificateSES() {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses("pedrinhodeccache@gmail.com"))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData("Your job was scheduled to remember you(If you want to stop the notificiations, don`t forget to check as complete"))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
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

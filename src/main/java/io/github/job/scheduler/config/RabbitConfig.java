package io.github.job.scheduler.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.jobs.pending.queue}")
    private String JOBS_PENDING_QUEUE;

    @Value("${rabbitmq.jobs.complete.queue}")
    private String JOBS_COMPLETE_QUEUE;

    @Value("${rabbitmq.jobs.exchange}")
    private String JOBS_EXCHANGE;

    @Value("${rabbitmq.jobs.routingkey.pending}")
    private String JOBS_ROUTINGKEY_PENDING;

    @Value("${rabbitmq.jobs.routingkey.complete}")
    private String JOBS_ROUTING_COMPLETE;

    @Bean
    public DirectExchange createExchange() {
        return new DirectExchange(JOBS_EXCHANGE);
    }

    @Bean
    public Queue jobPendingQueue() {
        return new Queue(JOBS_PENDING_QUEUE, true);
    }

    @Bean
    public Queue jobCompleteQueue() {
        return new Queue(JOBS_COMPLETE_QUEUE, true);
    }

    @Bean
    public Binding bindingJobCreatedQueue(DirectExchange createExchange,
                                          Queue jobPendingQueue){
        return BindingBuilder.bind(jobPendingQueue)
                .to(createExchange).with(JOBS_ROUTINGKEY_PENDING);
    }

    @Bean
    public Binding bindingJobComplete(DirectExchange createExchange,
                                      Queue jobCompleteQueue) {
        return BindingBuilder.bind(jobCompleteQueue)
                .to(createExchange).with(JOBS_ROUTING_COMPLETE);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin createRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> initializeAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        return rabbitTemplate;
    }

}

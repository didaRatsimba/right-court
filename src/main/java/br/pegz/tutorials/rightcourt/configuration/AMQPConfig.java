package br.pegz.tutorials.rightcourt.configuration;

import br.pegz.tutorials.rightcourt.serve.resource.CourtResource;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AMQPConfig {

    public static final String SCORE_QUEUE_EXCHANGE = "score-queue-exchange";
    public static final String PLAY_QUEUE_EXCHANGE = "plays-queue-exchange";
    public static final String SCORE_QUEUE = "score-queue";
    public static final String PLAYS_QUEUE = "plays-queue";


    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue scoreQueue() {
        return new Queue(SCORE_QUEUE);
    }

    @Bean
    public Queue playsQueue() {
        return new Queue(PLAYS_QUEUE);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PLAYS_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(CourtResource receiver) {
        return new MessageListenerAdapter(receiver, "receivePlay");
    }

}

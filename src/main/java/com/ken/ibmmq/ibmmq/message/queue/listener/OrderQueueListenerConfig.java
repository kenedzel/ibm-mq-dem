package com.ken.ibmmq.ibmmq.message.queue.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Arrays;
import java.util.List;

//Dynamic JMS listener configuration
@Configuration
@EnableJms
public class OrderQueueListenerConfig implements JmsListenerConfigurer {

    @Autowired
    private JmsTemplate jmsTemplate;
    List<String> queueNames = Arrays.asList("DEV.QUEUE.1", "DEV.QUEUE.2", "DEV.QUEUE.3", "DEV.QUEUE.4");

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        queueNames.forEach(queueName -> {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId(queueName);
            endpoint.setDestination(queueName);
            endpoint.setMessageListener(message -> {
                // processing
                try {
                    //message => Message
                    System.out.println("---------------" + message.getJMSDestination().toString() + " Message Received ---------------");
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println("Print From Config jms listener message handling: " + message.getJMSCorrelationID());
                    System.out.println("Identity Destination: " + message.getJMSDestination().toString());
                    System.out.println("Received Message: " + ((TextMessage) message).getText());
                    sendMessageReplyTo(message);
                    System.out.println("----------------------------------------");
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
            registrar.registerEndpoint(endpoint);
            System.out.println("Setup queue: " + queueName);
        });
    }

    private MessageConverter getRequiredMessageConverter() throws IllegalStateException {
        MessageConverter converter = jmsTemplate.getMessageConverter();
        if (converter == null) {
            throw new IllegalStateException("No 'messageConverter' specified. Check configuration of JmsTemplate.");
        } else {
            return converter;
        }
    }

    private void sendMessageReplyTo(Message message) throws JMSException {
        if ("queue:///DEV.QUEUE.1".equals(message.getJMSDestination().toString())) {
            //handle reply to
            jmsTemplate.send(message.getJMSReplyTo().toString(), session -> {
                Message jmsMessage = getRequiredMessageConverter().toMessage("Sending message to Queue2 From Queue1", session);
                jmsMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                jmsMessage.setJMSReplyTo(session.createQueue("DEV.QUEUE.3"));
                System.out.println("Sending Message to REPLY TO QUEUE: " + message.getJMSReplyTo().toString());
                return jmsMessage;
            });
        }
    }
}

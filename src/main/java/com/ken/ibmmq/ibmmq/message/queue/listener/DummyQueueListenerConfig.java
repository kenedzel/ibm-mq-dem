package com.ken.ibmmq.ibmmq.message.queue.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

@Configuration
@EnableJms
public class DummyQueueListenerConfig implements JmsListenerConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyQueueListenerConfig.class);

    @Autowired
    private JmsListenerEndpointRegistry registry;

    @Value("#{'${ibm.mq.queue.listening}'.split(',')}")
    private List<String> queueNames;

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
                    LOGGER.info("---------------" + message.getJMSDestination().toString() + " Message Received ---------------");
                    LOGGER.info("Print From Config jms listener message handling: " + message.getJMSCorrelationID());
                    LOGGER.info("Identity Destination: " + message.getJMSDestination().toString());
                    LOGGER.info("Received Message: " + ((TextMessage) message).getText());
                    LOGGER.info("----------------------------------------");
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
            registrar.setEndpointRegistry(registry);
            registrar.registerEndpoint(endpoint);
            LOGGER.info("Setup queue: " + queueName);
        });
    }
}

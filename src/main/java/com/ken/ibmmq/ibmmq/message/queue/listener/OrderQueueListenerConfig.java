package com.ken.ibmmq.ibmmq.message.queue.listener;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import javax.jms.JMSException;
import java.util.Arrays;
import java.util.List;

//Dynamic JMS listener configuration
@Configuration
@EnableJms
public class OrderQueueListenerConfig implements JmsListenerConfigurer {

    List<String> queueNames = Arrays.asList("DEV.QUEUE.1", "DEV.QUEUE.2", "DEV.QUEUE.3");

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
                    System.out.println("Print From Config jms listener message handling: " + message.getJMSCorrelationID());
                    System.out.println("Identity Destination: " + message.getJMSDestination().toString());
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
            registrar.registerEndpoint(endpoint);
        });

//        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
//        endpoint.setId("myJmsEndpoint");
//        endpoint.setDestination("DEV.QUEUE.1");
//        endpoint.setMessageListener(message -> {
//            // processing
//            try {
//                System.out.println("From Config jms listener: " + message.getJMSCorrelationID());
//            } catch (JMSException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        registrar.registerEndpoint(endpoint);
    }
}

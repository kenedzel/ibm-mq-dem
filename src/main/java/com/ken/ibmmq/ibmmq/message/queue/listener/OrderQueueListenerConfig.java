package com.ken.ibmmq.ibmmq.message.queue.listener;

import com.db.operator.dboperator.service.DeviceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ken.ibmmq.ibmmq.model.Device;
import org.springframework.beans.BeanUtils;
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
    private DeviceService deviceService;

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
                    System.out.println("Print From Config jms listener message handling: " + message.getJMSCorrelationID());
                    System.out.println("Identity Destination: " + message.getJMSDestination().toString());
                    System.out.println("Received Message: " + ((TextMessage) message).getText());
//                    sendMessageReplyTo(message); TEMP disable to test imported save function
                    //TODO: call imported save function
                    System.out.println("----------------------------------------");
                    System.out.println("Decoding message");
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object decoded = objectMapper.readValue(((TextMessage) message).getText(), Device.class);
                    com.db.operator.dboperator.model.Device device = new com.db.operator.dboperator.model.Device();
                    BeanUtils.copyProperties(decoded, device);
                    deviceService.saveDevice(device);
                    System.out.println("Fetching all data from H2DB....");
                    deviceService.fetchAllDevices().forEach(device1 ->
                    {
                        try {
                            System.out.println("Device: " + objectMapper.writeValueAsString(device1));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (JMSException | JsonProcessingException e) {
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

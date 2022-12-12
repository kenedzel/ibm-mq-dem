package com.ken.ibmmq.ibmmq.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mq.jms.MQQueue;
import com.ken.ibmmq.ibmmq.model.Device;
import com.ken.ibmmq.ibmmq.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

@RestController
public class SendController {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static Logger LOGGER = LoggerFactory.getLogger(SendController.class);
    @PostMapping("send")
    public ResponseEntity<String> send(@RequestHeader(value = "x-correlation-id") String correlationId, @RequestBody Order order) {
        return sendMessageToQueue("DEV.QUEUE.1", order, correlationId);
    }

    @PostMapping("send2")
    public ResponseEntity<String> sendQ2(@RequestHeader(value = "x-correlation-id") String correlationId, @RequestBody Order order) {
        return sendMessageToQueue("DEV.QUEUE.2", order, correlationId);
    }

    @PostMapping("send3")
    public ResponseEntity<String> sendQ3(@RequestHeader(value = "x-correlation-id") String correlationId, @RequestBody Order order) {
        return sendMessageToQueue("DEV.QUEUE.3", order, correlationId);
    }

    @PostMapping("senddevice")
    public ResponseEntity<String> sendQ3(@RequestHeader(value = "x-correlation-id") String correlationId, @RequestBody Device device) {
        return sendMessageToQueue("DEV.QUEUE.1", device, correlationId);
    }

    private ResponseEntity<String> sendMessageToQueue(String queueName, Object requestData, String correlationId) {
        try  {
            MQQueue sendMessageQueue = new MQQueue(queueName);
            ObjectMapper objectMapper = new ObjectMapper();
            String orderString = objectMapper.writeValueAsString(requestData);

            LOGGER.info("Sending data: {}", orderString);

            jmsTemplate.send(sendMessageQueue, session -> {
                Message message = getRequiredMessageConverter().toMessage(orderString, session);
                message.setJMSReplyTo(session.createQueue("DEV.QUEUE.2"));//try new MQQueue();
                message.setJMSCorrelationID(correlationId);
                return message;
            });
            return new ResponseEntity<>(orderString, HttpStatus.ACCEPTED);
        } catch(JmsException | JMSException | JsonProcessingException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MessageConverter getRequiredMessageConverter() throws IllegalStateException {
        MessageConverter converter = jmsTemplate.getMessageConverter();
        if (converter == null) {
            throw new IllegalStateException("No 'messageConverter' specified. Check configuration of JmsTemplate.");
        } else {
            return converter;
        }
    }
}

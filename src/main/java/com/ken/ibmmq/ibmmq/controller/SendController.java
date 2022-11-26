package com.ken.ibmmq.ibmmq.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mq.jms.MQQueue;
import com.ken.ibmmq.ibmmq.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

@RestController
@EnableJms
public class SendController {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static Logger LOGGER = LoggerFactory.getLogger(SendController.class);
    @PostMapping("send")
    public ResponseEntity<String> send(@RequestHeader(value = "x-correlation-id") String correlationId, @RequestBody Order order) {
        try{
            MQQueue sendMessageQueue = new MQQueue("DEV.QUEUE.1");
            ObjectMapper objectMapper = new ObjectMapper();
            String orderString = objectMapper.writeValueAsString(order);

            LOGGER.info("Sending data: {}", orderString);
            jmsTemplate.convertAndSend(sendMessageQueue, orderString, message -> {
                message.setJMSCorrelationID(correlationId);
                return message;
            });
        return new ResponseEntity<>(orderString, HttpStatus.ACCEPTED);
    }catch(JmsException | JMSException | JsonProcessingException ex){
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }
}

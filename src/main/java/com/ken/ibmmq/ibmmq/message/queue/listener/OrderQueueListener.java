package com.ken.ibmmq.ibmmq.message.queue.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;


//Traditional listener
//@EnableJms
//@Component
public class OrderQueueListener {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static Logger LOGGER = LoggerFactory.getLogger(OrderQueueListener.class);

    @JmsListener(destination = "DEV.QUEUE.1")
    public void recv(Message message){
        receiveMessage(message);
    }

    @JmsListener(destination = "DEV.QUEUE.1", selector = "JMSCorrelationID = 'testCorrelationId1'")
    public void receiveByJmsCorrelationId(Message message) {
        receiveMessage(message);
    }

    private void receiveMessage(Message message) {
        try{
            TextMessage textMessage = (TextMessage) message;
            LOGGER.info("Received data from queue: {} with Correlation ID: {}", ((TextMessage) message).getText(), textMessage.getJMSCorrelationID());
        }catch(JmsException | JMSException ex){
            ex.printStackTrace();
        }
    }
}

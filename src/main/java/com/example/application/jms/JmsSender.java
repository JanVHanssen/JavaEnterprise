package com.example.application.jms;

import com.example.application.dto.TodoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

@Component
public class JmsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${jms.queue-name}")
    private String queueName;

    public void sendMessage(TodoDTO todoDTO) {
        if (todoDTO == null) {
            System.err.println("TodoDTO is null");
            return;
        }
        if (queueName == null) {
            System.err.println("Queue name is null");
            return;
        }
        if (jmsTemplate == null) {
            System.err.println("JmsTemplate is null");
            return;
        }

        try {
            // Create JAXBContext and Marshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(TodoDTO.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            // Convert TodoDTO to string using JAXB marshalling
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(todoDTO, stringWriter);
            String message = stringWriter.toString();

            System.out.println("Sending message: " + message);

            // Send the message to the queue
            jmsTemplate.convertAndSend(queueName, message);
            System.out.println("Message sent: " + message);
        } catch (Exception e) {
            System.err.println("JMS Sending message on Queue error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

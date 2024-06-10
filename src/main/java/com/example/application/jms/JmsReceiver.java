package com.example.application.jms;

import com.example.application.dto.TodoDTO;
import com.example.application.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class JmsReceiver {

    @Autowired
    private TodoService todoService;

    @JmsListener(destination = "${jms.queue-name}")
    public void receiveMessage(String message) {
        try {
            System.out.println("Received message: " + message);

            // Create ObjectMapper with JavaTimeModule registered
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert the message from JSON to TodoDTO
            TodoDTO todoDTO = objectMapper.readValue(message, TodoDTO.class);

            // Pass the TodoDTO to the service for processing
            todoService.process(todoDTO);

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
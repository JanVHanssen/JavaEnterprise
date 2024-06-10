package com.example.application.soap;
import com.example.application.dto.TodoDTO;
import com.example.application.service.TodoService;

import com.example.application.soap.model.ObjectFactory;
import com.example.application.soap.model.STypeProcessOutcome;
import com.example.application.soap.model.TodoRequest;
import com.example.application.soap.model.TodoResponse;
import jakarta.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.namespace.QName;

@Endpoint
public class TodoEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(TodoEndpoint.class);
    private static final Logger msgLogger = LoggerFactory.getLogger("messagelogger");
    private static final String NAMESPACE_URI = "http://example.com/application/todo";

    @Autowired
    private TodoService todoService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "TodoRequest")
    @ResponsePayload
    public JAXBElement<TodoResponse> processMessage(@RequestPayload JAXBElement<TodoRequest> request) {
        TodoRequest req = request.getValue();

        TodoDTO todo = new TodoDTO();
        todo.setTodoId(req.getTodoId());
        todo.setTitle(req.getTitle());
        todo.setComment(req.getComment());
        todo.setCompleted(req.isCompleted());
        todo.setExpire(req.getExpire().toGregorianCalendar().toZonedDateTime().toLocalDate());
        todo.setUserId(req.getUserId());

        TodoResponse response = new TodoResponse();
        try {
            if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Mandatory title missing");
            }

            todoService.process(todo);

            response.setCode(0);
            response.setType(STypeProcessOutcome.INFO);
            logger.info("Todo saved successfully: {}", todo);
        } catch (IllegalArgumentException e) {
            response.setCode(1);
            response.setType(STypeProcessOutcome.ERROR);
            response.setFeedback(e.getMessage());
            logger.warn("Validation error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected exception occurred", e);
            response.setCode(1);
            response.setType(STypeProcessOutcome.ERROR);
            response.setFeedback("An unexpected exception occurred");
        }

        QName name = new QName(NAMESPACE_URI, "TodoResponse");
        return new JAXBElement<>(name, TodoResponse.class, response);
    }
}
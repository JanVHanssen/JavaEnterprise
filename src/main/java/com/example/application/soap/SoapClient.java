package com.example.application.soap;
import com.example.application.dto.TodoDTO;
import com.example.application.soap.model.TodoRequest;
import com.example.application.soap.model.TodoResponse;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class SoapClient extends WebServiceGatewaySupport {
    private String uri;

    public SoapClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.application.soap.model");
        this.setMarshaller(marshaller);
        this.setUnmarshaller(marshaller);
    }

    public String sendAndReceiveMessage(TodoDTO todo) {
        TodoRequest request = new TodoRequest();
        request.setTodoId(todo.getTodoId());
        request.setTitle(todo.getTitle());
        request.setComment(todo.getComment());
        request.setCompleted(todo.getCompleted());
        request.setExpire(convertToXMLGregorianCalendar(todo.getExpire()));
        request.setUserId(todo.getUserId());

        TodoResponse response = (TodoResponse) getWebServiceTemplate().marshalSendAndReceive(uri, request);

        if (response.getCode() == 0) {
            return null;
        } else {
            return response.getFeedback();
        }
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(LocalDate localDate) {
        try {
            GregorianCalendar gc = GregorianCalendar.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            logger.error("Error converting LocalDate to XMLGregorianCalendar", e);
            return null;
        }
    }
}
package com.example.application.soap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;

@EnableWs
@Configuration
public class TodoWebServiceConfig extends WsConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TodoWebServiceConfig.class);

    // http://localhost:8080/todo/soap/todoServiceSoap.wsdl
    @Bean(name = "todoServiceSoap")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema todoSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setTargetNamespace("http://example.com/application/todo");
        definition.setSchema(todoSchema);
        definition.setPortTypeName("TodoPort");
        definition.setLocationUri("/soap/todoreceiver");
        return definition;
    }

    @Bean
    public XsdSchema todoSchema() {
        Resource resource = new ClassPathResource("xsd/todo.xsd");
        logger.info("Loading schema from: " + resource);
        return new SimpleXsdSchema(resource);
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/soap/*");
    }
}
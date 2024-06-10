package com.example.application.views;

import com.example.application.jwt.AuthenticationRequest;
import com.example.application.model.User;
import com.example.application.service.TodoService;
import com.example.application.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;

import static org.springframework.data.support.PageableExecutionUtils.getPage;

@PageTitle("Login")
@Route("login")
public class LoginUI extends VerticalLayout {

    private final UserService userService;

    public LoginUI(UserService userService) {
        this.userService = userService;

        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Login");

        loginButton.addClickListener(event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();

            try {
                User user = userService.authenticate(email, password);
                VaadinSession.getCurrent().setAttribute("user", user);
                Notification.show("Login successful", 3000, Notification.Position.TOP_CENTER);
                // Navigate to the todo page
                getUI().ifPresent(ui -> ui.navigate("todo"));
            } catch (Exception e) {
                Notification.show("Login failed: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        Button signupButton = new Button("Sign Up");
        signupButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("signup")));

        add(emailField, passwordField, loginButton, signupButton);
    }
}
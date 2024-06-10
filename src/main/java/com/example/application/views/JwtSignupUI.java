package com.example.application.views;

import com.example.application.jwt.AuthenticationController;
import com.example.application.jwt.RegisterRequest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("JWT Signup")
@Route("jwt-signup")
public class JwtSignupUI extends VerticalLayout {

    private final AuthenticationController authenticationController;

    public JwtSignupUI(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;

        TextField emailField = new TextField("Email");
        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        PasswordField passwordField = new PasswordField("Password");
        Button signupButton = new Button("Sign Up");

        signupButton.addClickListener(event -> {
            String email = emailField.getValue();
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String password = passwordField.getValue();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(email);
            registerRequest.setFirstName(firstName);
            registerRequest.setLastName(lastName);
            registerRequest.setPlainPassword(password);

            try {
                authenticationController.registerUser(registerRequest);
                UI.getCurrent().getPage().setLocation("/todo/jwt-login");
            } catch (Exception e) {
                Notification.show("Registration failed: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        add(emailField, firstNameField, lastNameField, passwordField, signupButton);
    }
}

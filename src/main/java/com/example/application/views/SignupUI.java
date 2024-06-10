package com.example.application.views;

import com.example.application.model.User;
import com.example.application.service.UserService;
import com.example.application.jwt.RegisterRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Signup")
@Route("signup")
public class SignupUI extends VerticalLayout {

    private final UserService userService;

    public SignupUI(UserService userService) {
        this.userService = userService;

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

            User user = new User();
            user.setMail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPlainPassword(password);

            try {
                userService.save(user);
                Notification.show("User registered successfully", 3000, Notification.Position.TOP_CENTER);
                getUI().ifPresent(ui -> ui.navigate("login"));
            } catch (IllegalArgumentException e) {
                Notification.show("Email already exists", 3000, Notification.Position.TOP_CENTER);
            } catch (Exception e) {
                Notification.show("Registration failed: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        add(emailField, firstNameField, lastNameField, passwordField, signupButton);
    }
}
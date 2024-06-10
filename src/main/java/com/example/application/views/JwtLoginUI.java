package com.example.application.views;

import com.example.application.jwt.AuthenticationController;
import com.example.application.jwt.AuthenticationRequest;
import com.example.application.jwt.AuthenticationResponse;
import com.example.application.model.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

@PageTitle("JWT Login")
@Route("jwt-login")
public class JwtLoginUI extends VerticalLayout {

    private final AuthenticationController authenticationController;

    public JwtLoginUI(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;

        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Login");

        loginButton.addClickListener(event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();

            try {
                // Get the current Vaadin request
                VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
                if (vaadinRequest instanceof VaadinServletRequest) {
                    HttpServletRequest httpServletRequest = ((VaadinServletRequest) vaadinRequest).getHttpServletRequest();

                    // Make a request to your JWT login endpoint
                    ResponseEntity<AuthenticationResponse> response = authenticationController.createAuthenticationToken(
                            new AuthenticationRequest(email, password),
                            httpServletRequest
                    );

                    if (response.getStatusCode() == HttpStatus.OK) {
                        AuthenticationResponse authenticationResponse = response.getBody();
                        String jwtToken = authenticationResponse.getJwt();
                        User user = authenticationResponse.getUser();

                        // Store JWT token and user details for subsequent requests
                        VaadinSession.getCurrent().setAttribute("jwt", jwtToken);
                        VaadinSession.getCurrent().setAttribute("user", user);

                        // Navigate to the todo page
                        getUI().ifPresent(ui -> ui.navigate("todo"));

                        Notification.show("Login successful", 3000, Notification.Position.TOP_CENTER);
                    } else {
                        Notification.show("Login failed: Unexpected response", 3000, Notification.Position.TOP_CENTER);
                    }
                } else {
                    Notification.show("Login failed: Not an HTTP request", 3000, Notification.Position.TOP_CENTER);
                }
            } catch (Exception e) {
                Notification.show("Login failed: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        add(emailField, passwordField, loginButton);
    }
}
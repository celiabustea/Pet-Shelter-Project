package org.example.petshelterv0;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class HelloController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
        } else {
            messageLabel.setText("Login successful for: " + username);
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
        } else {
            messageLabel.setText("Registration successful for: " + username);
        }
    }
}

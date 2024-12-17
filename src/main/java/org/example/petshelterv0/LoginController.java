package org.example.petshelterv0;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform; // For UI updates on JavaFX thread

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    public Pane contentsPane;
    public Label emailLabel;
    public Label titleLabel;
    public Label passwordLabel;
    public Button loginButton;
    public Button registerButton;
    public Label usernameLabel;
    public Pane titlePane;
    public AnchorPane anchorPane;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField fullnameField;

    // Login button click event
    public void onLoginButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Login attempt with: " + email + ", " + username + ", " + password);

        ThreadManager.runInBackground(() -> {
            User user = DBHelper.getUser(email); // Fetch the user from the database
            if (user != null && user.getPassword().equals(password)) {
                boolean isAdmin = DBHelper.isAdmin(email); // Check if the user is an admin
                Platform.runLater(() -> {
                    if (isAdmin) {
                        showAlert("Login Successful", "Welcome, Admin " + user.getUsername() + "!");
                    } else {
                        showAlert("Login Successful", "Welcome, " + user.getUsername() + "!");
                    }
                    navigateToMainPage(); // Proceed to the main page
                });
            } else {
                Platform.runLater(() -> {
                    showAlert("Login Failed", "Incorrect email or password. Please try again.");
                });
            }
        });
    }

    // Register button click event
    @FXML
    protected void onRegisterButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String fullname = fullnameField.getText();

        System.out.println("Register attempt with: " + email + ", " + username + ", " + password + ", " + fullname);

        ThreadManager.runInBackground(() -> {
            boolean emailExists = DBHelper.isEmailExisting(email); // Check if email already exists
            Platform.runLater(() -> {
                if (emailExists) {
                    showAlert("Registration Failed", "Email already exists. Please choose another one.");
                } else {
                    ThreadManager.runInBackground(() -> {
                        User newUser = new User(username, password, email, fullname, "general");
                        DBHelper.saveUser(newUser); // Save the new user to the database
                        Platform.runLater(() -> {
                            showAlert("Registration Successful", "User registered successfully.");
                        });
                    });
                }
            });
        });
    }

    private void navigateToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petshelterv0/mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene mainScene = new Scene(root);
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load the main page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

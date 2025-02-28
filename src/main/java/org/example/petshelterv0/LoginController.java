package org.example.petshelterv0;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;

import java.io.IOException;


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

    public void onLoginButtonClick() {
        if (!validateInput(emailField.getText(), usernameField.getText(), passwordField.getText())) {
            return;
        }

        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Login attempt with: " + email + ", " + username + ", " + password);
        ThreadManager.runInBackground(() -> {
            User user = DBHelper.getUser(email);
            if (user != null && user.getPassword().equals(password)) {
                String role = DBHelper.getUserRole(email);
                Platform.runLater(() -> {
                    if ("admin".equalsIgnoreCase(role)) {
                        showAlert("Login Successful", "Welcome, Admin " + user.getUsername() + "!");
                    } else {
                        showAlert("Login Successful", "Welcome, " + user.getUsername() + "!");
                    }
                    navigateToMainPage(role);
                });
            } else {
                Platform.runLater(() -> {
                    showAlert("Login Failed", "Incorrect email or password. Please try again.");
                });
            }
        });
    }

    @FXML
    protected void onRegisterButtonClick() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String fullname = fullnameField.getText();

        System.out.println("Register attempt with: " + email + ", " + username + ", " + password + ", " + fullname);

        ThreadManager.runInBackground(() -> {
            boolean emailExists = DBHelper.isEmailExisting(email);
            Platform.runLater(() -> {
                if (emailExists) {
                    showAlert("Registration Failed", "Email already exists. Please choose another one.");
                } else {
                    ThreadManager.runInBackground(() -> {
                        User newUser = new User(username, password, email, fullname, "general");
                        DBHelper.saveUser(newUser);
                        Platform.runLater(() -> {
                            showAlert("Registration Successful", "User registered successfully.");
                        });
                    });
                }
            });
        });
    }

    private void navigateToMainPage(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petshelterv0/mainpage.fxml"));
            Parent root = loader.load();
            MainPageController controller = loader.getController();
            controller.setLoggedInUserEmail(emailField.getText());
            controller.setRole(role);

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

    private boolean validateInput(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                showAlert("Input Error", "All fields are required.");
                return false;
            }
        }

        if (!isValidEmail(emailField.getText())) {
            showAlert("Input Error", "Please enter a valid email address.");
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

}

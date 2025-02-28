package org.example.petshelterv0;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MainPageController {

    private String userRole;

    private String loggedInUserEmail;

    @FXML
    private GridPane petGridPane;

    @FXML
    private Button addPetButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Button deletePetButton;

    @FXML
    private Button submitRequestButton;

    @FXML
    private Button viewRequestsButton;

    @FXML
    private void onSubmitRequestClicked(ActionEvent event) {
        Alert choiceDialog = new Alert(Alert.AlertType.CONFIRMATION);
        choiceDialog.setTitle("Submit a Request");
        choiceDialog.setHeaderText("Would you like to adopt or surrender a pet?");
        choiceDialog.setContentText("Choose your request type:");

        ButtonType adoptButton = new ButtonType("Adopt");
        ButtonType surrenderButton = new ButtonType("Surrender");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        choiceDialog.getButtonTypes().setAll(adoptButton, surrenderButton, cancelButton);

        Optional<ButtonType> result = choiceDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == adoptButton) {
                showAdoptForm();
            } else if (result.get() == surrenderButton) {
                showSurrenderForm();
            }
        }
    }
    @FXML
    private void onViewRequestsClicked(ActionEvent event) {
        if ("admin".equalsIgnoreCase(userRole)) {
            showRequestsWindow();
        } else {
            showAlert("Permission Denied", "You must be an admin to view requests.");
        }
    }

    private void showRequestsWindow() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("RequestsPage.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Requests");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the requests page.");
        }
    }

    private void showAdoptForm() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Adopt a Pet");
        dialog.setHeaderText("Please provide the details for your adoption request:");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Pet Name");

        TextArea motivationField = new TextArea();
        motivationField.setPromptText("Why do you want to adopt this pet?");
        motivationField.setWrapText(true);

        grid.add(new Label("Pet Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Motivation:"), 0, 1);
        grid.add(motivationField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = nameField.getText().trim();
                String motivation = motivationField.getText().trim();

                if (name.isEmpty() || motivation.isEmpty()) {
                    showAlert("Error", "All fields must be filled out.");
                    return null;
                }
                saveAdoptionRequest(name, motivation);
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void showSurrenderForm() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Surrender a Pet");
        dialog.setHeaderText("Please provide the details for the pet you want to surrender:");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Pet Name");

        TextField ageField = new TextField();
        ageField.setPromptText("Age");

        TextField imagePathField = new TextField();
        imagePathField.setPromptText("Image Path");

        TextField speciesField = new TextField();
        speciesField.setPromptText("Species");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setWrapText(true);

        grid.add(new Label("Pet Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Age:"), 0, 1);
        grid.add(ageField, 1, 1);
        grid.add(new Label("Image Path:"), 0, 2);
        grid.add(imagePathField, 1, 2);
        grid.add(new Label("Species:"), 0, 3);
        grid.add(speciesField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = nameField.getText().trim();
                String age = ageField.getText().trim();
                String imagePath = imagePathField.getText().trim();
                String species = speciesField.getText().trim();
                String description = descriptionField.getText().trim();

                if (name.isEmpty() || age.isEmpty() || imagePath.isEmpty() || species.isEmpty() || description.isEmpty()) {
                    showAlert("Error", "All fields must be filled out.");
                    return null;
                }

                saveSurrenderRequest(name, age, imagePath, species, description);
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void saveAdoptionRequest(String name, String motivation) {
        String userId = getUserIdByEmail(loggedInUserEmail);
        if (userId == null) {
            showAlert("Error", "User not found.");
            return;
        }

        String query = "INSERT INTO petrequests (name, breed, description, user_id, request_date, status, request_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        DBHelper.executeUpdateQuery(query,
                name,
                "Unknown",
                motivation,
                userId,
                java.time.LocalDate.now().toString(),
                "Pending",
                "Adoption"
        );
        showAlert("Success", "Your adoption request has been submitted.");
    }


    private void saveSurrenderRequest(String name, String age, String imagePath, String species, String description) {
        String userId = getUserIdByEmail(loggedInUserEmail);
        if (userId == null) {
            showAlert("Error", "User not found.");
            return;
        }
        String query = "INSERT INTO petrequests (name, breed, description, user_id, request_date, status, request_type, image_path, age) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DBHelper.executeUpdateQuery(query,
                name,
                species,
                description,
                userId,
                java.time.LocalDate.now().toString(),
                "Pending",
                "Surrender",
                imagePath,
                age
        );

        showAlert("Success", "Your surrender request has been submitted.");
    }





    private String getUserIdByEmail(String email) {
        String query = "SELECT id FROM users WHERE email = ?";
        ResultSet rs = DBHelper.executeSelectQuery(query, email);
        try {
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private int currentRow = 0;

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        loadPets();
        updateAddPetButtonVisibility();
        updateSubmitRequestButtonVisibility();

    }
    private void updateSubmitRequestButtonVisibility() {
        submitRequestButton.setVisible(!"admin".equalsIgnoreCase(userRole));
    }
    public void setRole(String role) {
        this.userRole = role;
        if ("admin".equalsIgnoreCase(userRole)) {
            deletePetButton.setVisible(true);
            submitRequestButton.setVisible(false);
        } else {
            deletePetButton.setVisible(false);
            submitRequestButton.setVisible(true);
        }
        updateAddPetButtonVisibility();
    }
    private void updateAddPetButtonVisibility() {
        addPetButton.setVisible("admin".equalsIgnoreCase(userRole));
    }

    public void setLoggedInUserEmail(String email) {
        this.loggedInUserEmail = email;
    }



    private void loadPets() {
        try {
            ResultSet rs = DBHelper.getAllPets();
            int col = 0;
            int row = 0;

            petGridPane.setVgap(20);
            while (rs.next()) {

                final String name = rs.getString("name"); // Change petName to name
                final int age = rs.getInt("age");
                final String imagePath = rs.getString("image_path");
                final String breed = rs.getString("breed");
                final String description = rs.getString("description");

                ImageView petImageView = createPetImageView(imagePath, name, age, breed, description);
                VBox petInfoBox = createPetInfoBox(name, age, breed);

                HBox petHBox = new HBox(10);
                petHBox.setStyle("-fx-alignment: center; -fx-spacing: 10; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-max-width: 300; -fx-min-width: 300;");
                petHBox.getChildren().addAll(petImageView, petInfoBox);

                petGridPane.add(petHBox, col, row);

                col++;
                if (col == 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "There was an error loading the pets.");
        }
    }


    private ImageView createPetImageView(String imagePath, String name, int age, String breed, String description) {
        ImageView petImageView = new ImageView();
        Image image;


        String resourcePath = imagePath != null && !imagePath.isEmpty() ? imagePath : "/images/default_image.png";
        try {
            image = new Image(getClass().getResource(resourcePath).toExternalForm());
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("Error loading image: " + imagePath + ". " + e.getMessage());
            showAlert("Error loading image", "Please check if the image is in the resource folder and if the path is valid, the image will not be displayed.");
            image = new Image(getClass().getResource("/images/default_image.png").toExternalForm());
        }

        petImageView.setImage(image);


        double radius = 50.0;
        Circle clip = new Circle(radius, radius, radius);
        petImageView.setClip(clip);
        petImageView.setFitWidth(100);
        petImageView.setFitHeight(100);
        return petImageView;
    }


    private VBox createPetInfoBox(String name, int age, String breed) {
        VBox petInfoBox = new VBox();
        petInfoBox.setSpacing(5);
        petInfoBox.setStyle("-fx-alignment: center-left; -fx-max-width: 180;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label ageLabel = new Label("Age: " + age);
        ageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label breedLabel = new Label("Breed: " + breed);
        breedLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        petInfoBox.getChildren().addAll(nameLabel, ageLabel, breedLabel);

        return petInfoBox;
    }



    @FXML
    private void onAddPetClicked(ActionEvent event) {
        if ("admin".equalsIgnoreCase(userRole)) {
            showAddPetDialog();
        } else {
            showAlert("Permission Denied", "You must be an admin to add pets.");
        }
    }
    @FXML
    private void onDeletePetClicked(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Pet");
        dialog.setHeaderText("Enter the name of the pet to delete:");
        dialog.setContentText("Pet Name:");


        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String petName = result.get().trim();
            if (petName.isEmpty()) {
                showAlert("Error", "Pet name cannot be empty.");
                return;
            }
            boolean isDeleted = DBHelper.deletePetByName(petName);

            if (isDeleted) {
                petGridPane.getChildren().clear();
                currentRow = 0;
                loadPets();
                showAlert("Success", "Pet " + petName + " was successfully deleted.");
            } else {
                showAlert("Error", "Pet with the name " + petName + " not found.");
            }
        }
    }



    private void showAddPetDialog() {
        Dialog<Pet> dialog = new Dialog<>();
        dialog.setTitle("Add New Pet");
        dialog.setHeaderText("Enter the pet's details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        TextField imagePathField = new TextField();
        imagePathField.setPromptText("Image Path");
        TextField breedField = new TextField();
        breedField.setPromptText("Breed");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Age:"), 0, 1);
        grid.add(ageField, 1, 1);
        grid.add(new Label("Image Path:"), 0, 2);
        grid.add(imagePathField, 1, 2);
        grid.add(new Label("Breed:"), 0, 3);
        grid.add(breedField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int age = Integer.parseInt(ageField.getText());
                    return new Pet(
                            nameField.getText(),
                            age,
                            imagePathField.getText(),
                            breedField.getText(),
                            descriptionField.getText()
                    );
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid age format.");
                    return null;
                }
            }
            return null;
        });

        Optional<Pet> result = dialog.showAndWait();

        result.ifPresent(pet -> {
            DBHelper.savePet(pet);
            petGridPane.getChildren().clear();
            currentRow = 0;
            loadPets();
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

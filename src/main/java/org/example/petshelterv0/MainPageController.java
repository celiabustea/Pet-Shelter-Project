package org.example.petshelterv0;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ScrollPane;
import javafx.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MainPageController {

    @FXML
    private GridPane petGridPane;

    @FXML
    private Button addPetButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label titleLabel;


    private int currentRow = 0;

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        loadPets();
    }

    private void loadPets() {
        try {
            ResultSet rs = DBHelper.getAllPets();
            int col = 0;
            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String imagePath = rs.getString("image_path");
                String breed = rs.getString("breed");
                String description = rs.getString("description");

                ImageView petImageView = new ImageView();
                Image image;
                try {
                    //Try to load image, and print an error if it fails.
                    image = new Image(getClass().getResource(imagePath).toExternalForm());
                } catch(NullPointerException | IllegalArgumentException e) {
                    System.out.println("Error loading image " + imagePath + ". " + e);
                    showAlert("Error loading image: " + imagePath, "Please check if the image is in the resource folder and if the path is valid, the image will not be displayed");
                    //Set a default image if the resource can not be found.
                    image = new Image(getClass().getResource("/org/example/petshelterv0/images/default_image.png").toExternalForm());

                }
                petImageView.setImage(image);
                // Make the image circular
                double radius = 50.0;
                Circle clip = new Circle(radius, radius, radius);
                petImageView.setClip(clip);
                petImageView.setFitWidth(100);
                petImageView.setFitHeight(100);

                VBox petInfoBox = new VBox();
                petInfoBox.setSpacing(5);
                petInfoBox.setStyle("-fx-alignment: center-left; -fx-max-width: 180;");

                petInfoBox.getChildren().addAll(
                        new Label(name){{setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");}},
                        new Label("Age: " + age){{setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");}},
                        new Label("Breed: " + breed){{setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");}}
                );

                HBox petHBox = new HBox(10);
                petHBox.setStyle("-fx-alignment: center; -fx-spacing: 10; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-max-width: 300; -fx-min-width: 300;");
                petHBox.getChildren().addAll(petImageView, petInfoBox);

                petGridPane.add(petHBox, col, currentRow);
                col = (col +1) % 2;
                if(col == 0){
                    currentRow++;
                }
                petGridPane.setMaxWidth(800);
                petGridPane.setPadding(new Insets(20));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "There was an error loading the pets.");
        }
    }
    @FXML
    private void onAddPetClicked(ActionEvent event) {
        Dialog<Pet> dialog = new Dialog<>();
        dialog.setTitle("Add New Pet");
        dialog.setHeaderText("Enter the pet's details:");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

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
        grid.add(new Label("Description"),0, 4);
        grid.add(descriptionField, 1, 4);
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Pet object when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try{
                    int age = Integer.parseInt(ageField.getText());

                    return new Pet(nameField.getText(), age, imagePathField.getText(), breedField.getText(), descriptionField.getText());

                } catch(NumberFormatException e){
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
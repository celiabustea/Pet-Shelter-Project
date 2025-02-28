package org.example.petshelterv0;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.example.petshelterv0.DBHelper.savePet;
import static org.example.petshelterv0.DBHelper.deletePetByName;

public class RequestsPageController {
    @FXML
    private TableColumn<Request, Integer> idColumn;
    @FXML
    private TableColumn<Request, String> userIdColumn;
    @FXML private TableView<Request> requestsTableView;
    @FXML private TableColumn<Request, String> nameColumn;
    @FXML private TableColumn<Request, String> breedColumn;
    @FXML private TableColumn<Request, String> descriptionColumn;
    @FXML private TableColumn<Request, String> statusColumn;
    @FXML private TableColumn<Request, String> requestTypeColumn;
    @FXML private TableColumn<Request, String> requestDateColumn;
    @FXML private TableColumn<Request, String> actionsColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        userIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName())); // Updated
        breedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBreed())); // Updated
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus())); // Updated
        requestTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestType()));
        requestDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestDate()));

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button denyButton = new Button("Deny");
            private final HBox actionBox = new HBox(10, acceptButton, denyButton);

            {
                acceptButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    onAcceptButtonClicked(null);
                });

                denyButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleDeny(request);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        loadRequests();
    }

    private void loadRequests() {
        List<Request> requests = DBHelper.getAllRequestsForAdmin();
        ObservableList<Request> observableRequests = FXCollections.observableArrayList(requests);
        requestsTableView.setItems(observableRequests);
    }

    @FXML
    private void onAcceptButtonClicked(ActionEvent event) {
        Request selectedRequest = requestsTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            if ("Surrender".equals(selectedRequest.getRequestType())) {
                handleSurrenderAccept(selectedRequest);
            } else if ("Adoption".equals(selectedRequest.getRequestType())) {
                handleAdoptAccept(selectedRequest);
            } else {
                showAlert("Error", "Unknown request type.");
            }
        } else {
            showAlert("Error", "No request selected.");
        }
    }



    private void handleSurrenderAccept(Request request) {
        System.out.println("Checking if pet exists: " + request.getName());
        if (DBHelper.isPetExists(request.getName())) {
            showAlert("Error", "This pet already exists in the database.");
            return; 
        }

        System.out.println("Pet doesn't exist, proceeding with surrender.");
        Pet surrenderedPet = new Pet(
                request.getName(),
                request.getAge(),
                request.getImagePath(),
                request.getBreed(),
                request.getDescription()
        );
        savePet(surrenderedPet);
        updateRequestStatus(request, "Accepted");
        showAlert("Success", "Surrender request accepted. Pet added to the database.");
    }


    private void handleAdoptAccept(Request request) {
        boolean petDeleted = deletePetByName(request.getName());
        if (petDeleted) {
            updateRequestStatus(request, "Adopted");
            showAlert("Success", "Adoption request accepted. Pet has been removed from the database.");
            requestsTableView.getItems().remove(request);
        } else {
            showAlert("Error", "Failed to delete the pet from the database.");
        }
    }

    private void updateRequestStatus(Request request, String status) {
        String query = "UPDATE petrequests SET status = ? WHERE name = ?";
        DBHelper.executeUpdateQuery(query, status, request.getName());
        System.out.println("Updated request status for pet: " + request.getName() + " to: " + status);
        if ("Accepted".equals(status)) {
            String insertPetQuery = "INSERT INTO pets (name, age, image_path, breed, description) "
                    + "SELECT name, age, image_path, breed, description "
                    + "FROM petrequests WHERE name = ?";
            try (Connection connection = DBHelper.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(insertPetQuery)) {
                stmt.setString(1, request.getName());
                stmt.executeUpdate();
                System.out.println("Pet added to database from request: " + request.getName());
            } catch (SQLException e) {
                System.err.println("Error inserting pet into database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void handleDeny(Request request) {
        updateRequestStatus(request, "Denied");
        showAlert("Success", "Request has been denied.");
        loadRequests();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

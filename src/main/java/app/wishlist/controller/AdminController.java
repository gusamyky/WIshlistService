package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.service.SecretSantaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class AdminController {

    private final DataService dataService = DataService.getInstance();
    private final SecretSantaService secretSantaService = SecretSantaService.getInstance();
    // Observable lists to track movement
    private final ObservableList<User> availableUsers = FXCollections.observableArrayList();
    private final ObservableList<User> selectedUsers = FXCollections.observableArrayList();
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView<User> availableList;
    @FXML
    private ListView<User> selectedList;

    @FXML
    public void initialize() {
        // 1. Setup Lists
        availableUsers.addAll(dataService.getAllUsers());

        availableList.setItems(availableUsers);
        selectedList.setItems(selectedUsers);

        // 2. Formatting (Names instead of objects)
        Callback<ListView<User>, ListCell<User>> cellFactory = param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getFullName());
            }
        };
        availableList.setCellFactory(cellFactory);
        selectedList.setCellFactory(cellFactory);
    }

    @FXML
    private void handleAdd() {
        User selected = availableList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            availableUsers.remove(selected);
            selectedUsers.add(selected);
        }
    }

    @FXML
    private void handleRemove() {
        User selected = selectedList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedUsers.remove(selected);
            availableUsers.add(selected);
        }
    }

    @FXML
    private void handleDraw() {
        if (datePicker.getValue() == null) {
            showAlert("Please select an event date.");
            return;
        }
        if (selectedUsers.size() < 2) {
            showAlert("You need at least 2 participants!");
            return;
        }

        // PERFORM DRAW
        secretSantaService.setEventDate(datePicker.getValue());
        secretSantaService.performDraw(selectedUsers);

        // Success Message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw Complete");
        alert.setHeaderText("Secret Santa Pairs Assigned!");
        alert.setContentText("The pairs have been generated. Users can now log in to see their target.");
        alert.showAndWait();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

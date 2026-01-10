package app.wishlist.controller;

import app.wishlist.model.SecretSantaEvent;
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
    private final ObservableList<User> availableUsers = FXCollections.observableArrayList();
    private final ObservableList<User> selectedUsers = FXCollections.observableArrayList();
    @FXML
    private DatePicker datePicker; // Note: We might display this read-only or allow edits
    @FXML
    private ListView<User> availableList;
    @FXML
    private ListView<User> selectedList;
    private SecretSantaEvent currentEvent;

    @FXML
    public void initialize() {
        // Setup formatting
        Callback<ListView<User>, ListCell<User>> cellFactory = param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getFullName() + " (@" + item.getLogin() + ")");
            }
        };
        availableList.setCellFactory(cellFactory);
        selectedList.setCellFactory(cellFactory);

        availableList.setItems(availableUsers);
        selectedList.setItems(selectedUsers);
    }

    public void setEvent(SecretSantaEvent event) {
        this.currentEvent = event;
        if (event.getLocalDate() != null) {
            datePicker.setValue(event.getLocalDate());
        }
        refreshLists();
    }

    private void refreshLists() {
        availableUsers.clear();
        selectedUsers.clear();

        // 1. Load Selected Users (Participants in the event)
        for (String login : currentEvent.getParticipantLogins()) {
            User u = dataService.getUserByLogin(login);
            if (u != null) selectedUsers.add(u);
        }

        // 2. Load Available Users (Friends of the logged in user + All users if Admin)
        // For simplicity, let's load ALL users minus the ones already selected
        for (User u : dataService.getAllUsers()) {
            if (!currentEvent.getParticipantLogins().contains(u.getLogin())) {
                availableUsers.add(u);
            }
        }
    }

    @FXML
    private void handleAdd() {
        User selected = availableList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Update UI
            availableUsers.remove(selected);
            selectedUsers.add(selected);
            // Update Model immediately
            secretSantaService.addParticipant(currentEvent, selected);
        }
    }

    @FXML
    private void handleRemove() {
        User selected = selectedList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            availableUsers.add(selected);
            selectedUsers.remove(selected);
            secretSantaService.removeParticipant(currentEvent, selected);
        }
    }

    @FXML
    private void handleDraw() {
        if (selectedUsers.size() < 4) { // Requirement: Min 4
            showAlert("You need at least 4 participants!");
            return;
        }

        secretSantaService.performDraw(currentEvent);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw Complete");
        alert.setHeaderText("Pairs Assigned!");
        alert.setContentText("The event has been updated.");
        alert.showAndWait();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

package app.wishlist.controller;

import app.wishlist.model.domain.event.SecretSantaEvent;
import app.wishlist.model.domain.user.User;
import app.wishlist.service.impl.DataServiceImpl;
import app.wishlist.service.impl.SecretSantaServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Files;

public class AdminController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
    private final SecretSantaServiceImpl secretSantaService = SecretSantaServiceImpl.getInstance();
    private final ObservableList<User> availableUsers = FXCollections.observableArrayList();
    private final ObservableList<User> selectedUsers = FXCollections.observableArrayList();
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView<User> availableList;
    @FXML
    private ListView<User> selectedList;
    private SecretSantaEvent currentEvent;

    @FXML
    public void initialize() {
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
        loadSelectedUsers();
        loadAvailableUsers();
    }

    private void loadSelectedUsers() {
        selectedUsers.clear();

        for (String login : currentEvent.getParticipantLogins()) {
            User u = dataService.getUserByLogin(login);
            if (u != null)
                selectedUsers.add(u);
        }
    }

    private void loadAvailableUsers() {
        availableUsers.clear();

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
            availableUsers.remove(selected);
            selectedUsers.add(selected);

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
        if (selectedUsers.size() < 4) {
            showAlert("Warning", "You need at least 4 participants!");
            return;
        }

        secretSantaService.performDraw(currentEvent);

        showAlert("Draw Complete", "The event has been updated. Pairs Assigned!");
    }

    @FXML
    private void handleViewReports() {
        if (currentEvent == null)
            return;

        File dir = new File("reports");
        String filename = "event_" + currentEvent.getId() + "_report.txt";
        File file = new File(dir, filename);

        if (!file.exists()) {
            showAlert("Info", "No feedback reports found for this event yet.");
            return;
        }

        try {
            String content = Files.readString(file.toPath());

            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(500, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Event Feedback Report");
            alert.setHeaderText("Feedback for: " + currentEvent.getName());
            alert.getDialogPane().setContent(textArea);
            alert.setResizable(true);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error reading report file.");
        }
    }
}

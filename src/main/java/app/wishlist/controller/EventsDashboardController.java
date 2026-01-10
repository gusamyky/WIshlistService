package app.wishlist.controller;

import app.wishlist.model.SecretSantaEvent;
import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.service.SecretSantaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EventsDashboardController {

    private final SecretSantaService eventService = SecretSantaService.getInstance();
    private final DataService dataService = DataService.getInstance();
    @FXML
    private TextField eventNameField;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private ListView<SecretSantaEvent> eventsList;
    private MainLayoutController mainLayoutController;

    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    @FXML
    public void initialize() {
        refreshList();

        // Custom Cell to show details
        eventsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(SecretSantaEvent event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    String status = event.isDrawDone() ? "[DRAW DONE]" : "[PLANNING]";
                    setText(event.getName() + " (" + event.getLocalDate() + ") - " + status);
                }
            }
        });

        // Click Logic
        eventsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && mainLayoutController != null) {
                mainLayoutController.navToEventDetails(newVal);
            }
        });
    }

    @FXML
    private void handleCreate() {
        if (eventNameField.getText().isBlank() || eventDatePicker.getValue() == null) {
            showAlert("Please enter a name and date.");
            return;
        }

        User me = dataService.getLoggedInUser();
        eventService.createEvent(eventNameField.getText(), me, eventDatePicker.getValue());

        eventNameField.clear();
        refreshList();
    }

    private void refreshList() {
        User me = dataService.getLoggedInUser();
        eventsList.getItems().setAll(eventService.getMyEvents(me));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

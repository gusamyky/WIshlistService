package app.wishlist.controller;

import app.wishlist.model.SecretSantaEvent;
import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.service.SecretSantaService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Setter;

public class EventsDashboardController extends BaseController {

    private final SecretSantaService eventService = SecretSantaService.getInstance();
    private final DataService dataService = DataService.getInstance();
    @FXML
    private TextField eventNameField;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private ListView<SecretSantaEvent> eventsList;
    @Setter
    private MainLayoutController mainLayoutController;

    @FXML
    public void initialize() {
        refreshList();

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
                System.out.println("Navigating to Event Details from Dashboard for Event: " + newVal.getName());
                mainLayoutController.navToEventDetails(newVal);
            }
        });
    }

    @FXML
    private void handleCreate() {
        if (eventNameField.getText().isBlank() || eventDatePicker.getValue() == null) {
            showError("Please enter a name and date.");
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
}

package app.wishlist.controller;

import app.wishlist.model.domain.event.SecretSantaEvent;
import app.wishlist.model.domain.event.SecretSantaSatisfactionQuestionnaire;
import app.wishlist.model.domain.user.User;
import app.wishlist.model.report.ReportInterface;
import app.wishlist.service.impl.DataServiceImpl;
import app.wishlist.service.impl.SecretSantaServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FeedbackController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
    private final SecretSantaServiceImpl eventService = SecretSantaServiceImpl.getInstance();
    @FXML
    private ComboBox<SecretSantaEvent> eventComboBox;
    @FXML
    private Slider ratingSlider;
    @FXML
    private TextArea commentArea;
    @FXML
    private CheckBox againCheck;

    @FXML
    public void initialize() {
        // 1. Configure ComboBox to show Event Names
        eventComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SecretSantaEvent event) {
                return event == null ? "" : event.getName() + " (" + event.getLocalDate() + ")";
            }

            @Override
            public SecretSantaEvent fromString(String string) {
                return null;
            }
        });

        // 2. Load Events where I am a participant
        User me = dataService.getLoggedInUser();
        eventComboBox.getItems().setAll(eventService.getMyEvents(me));
    }

    @FXML
    private void handleSubmit() {
        SecretSantaEvent selectedEvent = eventComboBox.getValue();
        if (selectedEvent == null) {
            showError("Please select an event first.");
            return;
        }

        // 1. Create Report Object
        ReportInterface report = new SecretSantaSatisfactionQuestionnaire(
                dataService.getLoggedInUser(),
                (int) ratingSlider.getValue(),
                commentArea.getText(),
                againCheck.isSelected());

        // 2. Save to the specific EVENT file (Append Mode)
        saveReportToEventFile(selectedEvent, report);

        // 3. Success
        showAlert("Thank You", "Your feedback has been appended to the event report.");

        commentArea.clear();
        ratingSlider.setValue(5);
    }

    private void saveReportToEventFile(SecretSantaEvent event, ReportInterface report) {
        File dir = new File("reports");
        if (!dir.exists())
            dir.mkdirs();

        // One file per event
        String filename = "event_" + event.getId() + "_report.txt";
        File file = new File(dir, filename);

        // Use 'true' in FileWriter constructor for APPEND mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("----- FEEDBACK FROM: " + report.getAuthor() + " -----\n");
            writer.write(report.getReportContent());
            writer.write("\n------------------------------------------------\n\n");
            System.out.println("Appended feedback to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to save report.");
        }
    }
}

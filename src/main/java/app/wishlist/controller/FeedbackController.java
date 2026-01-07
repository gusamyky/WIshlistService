package app.wishlist.controller;

import app.wishlist.model.ReportInterface;
import app.wishlist.model.SecretSantaSatisfactionQuestionnaire;
import app.wishlist.service.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

public class FeedbackController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private Slider ratingSlider;
    @FXML
    private TextArea commentArea;
    @FXML
    private CheckBox againCheck;

    @FXML
    private void handleSubmit() {
        // 1. Create the Report Object
        ReportInterface report = new SecretSantaSatisfactionQuestionnaire(
                dataService.getLoggedInUser(),
                (int) ratingSlider.getValue(),
                commentArea.getText(),
                againCheck.isSelected()
        );

        // 2. "Process" the report (Print to console)
        System.out.println("----- NEW REPORT SUBMITTED -----");
        System.out.println("Title: " + report.getReportTitle());
        System.out.println("Content:\n" + report.getReportContent());
        System.out.println("--------------------------------");

        // 3. Show Success
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thank You");
        alert.setHeaderText("Feedback Received");
        alert.setContentText("Thanks for your feedback! See you next year.");
        alert.showAndWait();

        // Optional: Clear fields
        commentArea.clear();
        ratingSlider.setValue(5);
    }
}

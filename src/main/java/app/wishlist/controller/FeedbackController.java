package app.wishlist.controller;

import app.wishlist.model.ReportInterface;
import app.wishlist.model.SecretSantaSatisfactionQuestionnaire;
import app.wishlist.service.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileWriter;

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
        ReportInterface report = new SecretSantaSatisfactionQuestionnaire(
                dataService.getLoggedInUser(),
                (int) ratingSlider.getValue(),
                commentArea.getText(),
                againCheck.isSelected()
        );

        saveReportToFile(report); // Call the new save method

        // Show Success
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thank You");
        alert.setHeaderText("Feedback Received");
        alert.setContentText("Your feedback has been saved.");
        alert.showAndWait();

        commentArea.clear();
        ratingSlider.setValue(5);
    }

    private void saveReportToFile(ReportInterface report) {
        // 1. Ensure directory exists
        File dir = new File("reports");
        if (!dir.exists()) dir.mkdirs();

        // 2. Create unique filename: report_jdoe_12345678.txt
        String filename = "report_" + report.getAuthor() + "_" + System.currentTimeMillis() + ".txt";
        File file = new File(dir, filename);

        // 3. Write content
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("TITLE: " + report.getReportTitle() + "\n");
            writer.write("--------------------------------\n");
            writer.write(report.getReportContent());
            System.out.println("Report saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to save report.");
        }
    }
}

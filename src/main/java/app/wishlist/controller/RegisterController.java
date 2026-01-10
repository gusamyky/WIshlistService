package app.wishlist.controller;

import app.wishlist.service.DataService;
import app.wishlist.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private TextField loginField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passField;

    @FXML
    private void handleRegister() {
        if (loginField.getText().isBlank() || firstNameField.getText().isBlank()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        boolean success = dataService.registerUser(
                loginField.getText().trim(),
                passField.getText(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                false
        );

        if (success) {
            showAlert("Success", "Account created! Please log in.");
            ViewSwitcher.switchTo(ViewSwitcher.LOGIN);
        } else {
            showAlert("Error", "Username '" + loginField.getText() + "' is already taken.");
        }
    }

    @FXML
    private void handleBack() {
        ViewSwitcher.switchTo(ViewSwitcher.LOGIN);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

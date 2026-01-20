package app.wishlist.controller;

import app.wishlist.consts.AppRoutes;
import app.wishlist.service.impl.DataServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
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
            showError("Please fill in all fields.");
            return;
        }

        boolean success = dataService.registerUser(
                loginField.getText().trim(),
                passField.getText(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                false);

        if (success) {
            showAlert("Success", "Account created! Please log in.");
            navigate(AppRoutes.LOGIN);
        } else {
            showError("Username '" + loginField.getText() + "' is already taken.");
        }
    }

    @FXML
    private void handleBack() {
        navigate(AppRoutes.LOGIN);
    }
}

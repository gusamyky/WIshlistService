package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showError("Please enter credentials");
            return;
        }

        // Authenticate via Service
        boolean isAuthenticated = dataService.authenticate(login, password);

        if (isAuthenticated) {
            // Retrieve full user object to set context
            User user = dataService.getUserByLogin(login);
            dataService.setLoggedInUser(user);

            // Navigate
            ViewSwitcher.switchTo(ViewSwitcher.MAIN_LAYOUT);
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleGoToRegister() {
        ViewSwitcher.switchTo("/fxml/register-view.fxml");
    }
}

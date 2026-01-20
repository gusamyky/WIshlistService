package app.wishlist.controller;

import app.wishlist.consts.AppRoutes;
import app.wishlist.model.domain.user.User;
import app.wishlist.service.impl.DataServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showError("Please enter credentials");
            return;
        }

        boolean isAuthenticated = dataService.authenticate(login, password);

        if (isAuthenticated) {
            User user = dataService.getUserByLogin(login);
            dataService.setLoggedInUser(user);

            navigate(AppRoutes.MAIN_LAYOUT);
        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleGoToRegister() {
        navigate(AppRoutes.REGISTER);
    }
}

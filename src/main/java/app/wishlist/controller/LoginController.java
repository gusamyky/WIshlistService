package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class LoginController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private ComboBox<User> userCombo;

    @FXML
    public void initialize() {
        // 1. Populate the ComboBox with users
        userCombo.getItems().addAll(dataService.getAllUsers());

        // 2. Make the ComboBox display Names instead of object hashes
        userCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() + " (" + user.getLogin() + ")" : "";
            }

            @Override
            public User fromString(String string) {
                return null; // Not needed for read-only selection
            }
        });
    }

    @FXML
    private void handleLogin() {
        User selected = userCombo.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dataService.setLoggedInUser(selected);
            // Switch to Main Layout
            ViewSwitcher.switchTo(ViewSwitcher.MAIN_LAYOUT);
        }
    }
}

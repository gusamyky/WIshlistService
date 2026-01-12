package app.wishlist.controller;

import javafx.scene.control.Alert;

/**
 * Abstract Base Class for Controllers.
 * Satisfies the OOP requirement for "Abstract classes".
 * Provides common utility methods for UI alerts.
 */
public abstract class BaseController {

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void showError(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

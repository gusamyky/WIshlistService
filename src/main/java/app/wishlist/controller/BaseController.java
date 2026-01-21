package app.wishlist.controller;

import app.wishlist.view.ViewSwitcher;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Base controller providing common functionalities for other controllers.
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected void logError(String message, Exception e) {
        logger.error(message, e);
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
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

    protected void navigate(String fxmlPath) {
        ViewSwitcher.switchTo(fxmlPath);
    }
}

package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.view.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainLayoutController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private Label userNameLabel;
    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // 1. Setup User Info
        User currentUser = dataService.getLoggedInUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
        }

        // 2. Load Default View (My Wishlist)
        navToWishlist();
    }

    // --- Navigation Actions ---

    @FXML
    private void navToWishlist() {
        loadView("/fxml/wishlist-view.fxml");
    }

    @FXML
    private void navToFriends() {
        // Placeholder for now
        System.out.println("Navigating to Friends...");
        // loadView("/fxml/friends-view.fxml");
    }

    @FXML
    private void navToSecretSanta() {
        // Placeholder
        System.out.println("Navigating to Secret Santa...");
    }

    @FXML
    private void handleLogout() {
        dataService.logout();
        ViewSwitcher.switchTo(ViewSwitcher.LOGIN);
    }

    // --- Helper Method to Swap Views ---

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Clear current content and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load sub-view: " + fxmlPath);
        }
    }
}

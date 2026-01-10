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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends-view.fxml"));
            Parent view = loader.load();

            // Pass reference so FriendsController can call us back
            FriendsController controller = loader.getController();
            controller.setMainLayoutController(this);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navToFriendWishlist(User friend) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wishlist-view.fxml"));
            Parent view = loader.load();

            // Setup the controller for the Friend
            WishlistController controller = loader.getController();
            controller.setup(friend);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navToSecretSanta() {
        // Simple Admin Check (Hardcoded for prototype)
        User currentUser = dataService.getLoggedInUser();
        boolean isAdmin = currentUser.isAdmin();
        System.out.println("Navigating to Secret Santa view. Is Admin: " + isAdmin);

        String fxml = isAdmin ? "/fxml/admin-view.fxml" : "/fxml/reveal-view.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();

            // If it's the Reveal view, pass the controller reference so we can link to the wishlist
            if (!isAdmin && loader.getController() instanceof RevealController) {
                ((RevealController) loader.getController()).setMainLayoutController(this);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navToFeedback() {
        loadView("/fxml/feedback-view.fxml");
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

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

public class MainLayoutController extends BaseController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private Label userNameLabel;
    @FXML
    private StackPane contentArea;

    // Add a field to track current view
    private String currentView = "";

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

    public void navToFriendWishlist(User friend) {
        loadView("/fxml/wishlist-view.fxml");
    }

    @FXML
    private void navToSecretSanta() {
        loadView("/fxml/events-dashboard.fxml");
    }

    public void navToEventDetails(app.wishlist.model.SecretSantaEvent event) {
        User me = dataService.getLoggedInUser();

        boolean canManage = me.isAdmin() || event.getOwnerLogin().equals(me.getLogin());
        String fxml = canManage ? "/fxml/admin-view.fxml" : "/fxml/reveal-view.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();

            if (canManage) {
                AdminController controller = loader.getController();
                controller.setEvent(event);
            } else {
                RevealController controller = loader.getController();
                controller.setMainLayoutController(this);
                controller.setEvent(event);
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
    private void navToFriends() {
        loadView("/fxml/friends-view.fxml");
    }

    @FXML
    private void handleLogout() {
        dataService.logout();
        ViewSwitcher.switchTo(ViewSwitcher.LOGIN);
    }

    // --- Helper Method to Swap Views ---

    private void loadView(String fxmlPath) {
        // REQUIREMENT: Prevent reloading if already active
        if (currentView.equals(fxmlPath)) {
            System.out.println("View already active: " + fxmlPath);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Inject dependencies if needed
            Object controller = loader.getController();
            if (controller instanceof FriendsController) {
                ((FriendsController) controller).setMainLayoutController(this);
            }
            // ... add other injections here ...

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Update tracker
            currentView = fxmlPath;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

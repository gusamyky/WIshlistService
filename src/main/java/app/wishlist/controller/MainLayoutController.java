package app.wishlist.controller;

import app.wishlist.consts.AppRoutes;
import app.wishlist.model.User;
import app.wishlist.service.DataService;
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

    private String currentView = "";

    @FXML
    public void initialize() {
        User currentUser = dataService.getLoggedInUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
        }


        navToWishlist();
    }


    @FXML
    private void navToWishlist() {
        loadView(AppRoutes.WISHLIST);
    }

    public void navToFriendWishlist(User friend) {
//        loadView(AppRoutes.WISHLIST);
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
        loadView(AppRoutes.EVENTS_DASHBOARD);
    }

    public void navToEventDetails(app.wishlist.model.SecretSantaEvent event) {
        User me = dataService.getLoggedInUser();

        boolean canManage = me.isAdmin() || event.getOwnerLogin().equals(me.getLogin());
        String fxml = canManage ? AppRoutes.ADMIN : AppRoutes.REVEAL;

        System.out.println("Navigating to Event Details. Can Manage: " + canManage + " Event: " + event.getName()
                + " for User: " + me.getLogin() + "Route: " + fxml);

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
        loadView(AppRoutes.FEEDBACK);
    }

    @FXML
    private void navToFriends() {
        loadView(AppRoutes.FRIENDS);
    }

    @FXML
    private void handleLogout() {
        dataService.logout();
        navigate(AppRoutes.LOGIN);
    }

    /// Helper method to load a view.
    private void loadView(String fxmlPath) {
        if (currentView.equals(fxmlPath)) {
            System.out.println("View already active: " + fxmlPath);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FriendsController) {
                ((FriendsController) controller).setMainLayoutController(this);
            } else if (controller instanceof EventsDashboardController) {
                ((EventsDashboardController) controller).setMainLayoutController(this);
            } else

                contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            currentView = fxmlPath;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

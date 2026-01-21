package app.wishlist.controller;

import app.wishlist.consts.AppRoutes;
import app.wishlist.model.domain.event.SecretSantaEvent;
import app.wishlist.model.domain.user.User;
import app.wishlist.service.impl.DataServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class MainLayoutController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
    @FXML
    public ImageView userAvatar;
    @FXML
    private Label userNameLabel;
    @FXML
    private StackPane contentArea;

    private String currentView = "";
    private String previousView = "";
    private SecretSantaEvent currentEvent = null;

    @FXML
    public void initialize() {
        User currentUser = dataService.getLoggedInUser();

        Image userAvatarImage = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/user_avatar.png")));

        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
            userAvatar.setImage(userAvatarImage);
        }

        navToWishlist();
    }

    @FXML
    public void navToWishlist() {
        loadView(AppRoutes.WISHLIST);
    }

    public void navToFriendWishlist(User friend) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wishlist-view.fxml"));
            Parent view = loader.load();

            WishlistController controller = loader.getController();
            controller.setMainLayoutController(this);
            controller.setup(friend);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            if (AppRoutes.REVEAL.equals(currentView) || AppRoutes.ADMIN.equals(currentView)) {
                previousView = currentView;
            } else {
                previousView = currentView.isEmpty() ? AppRoutes.FRIENDS : currentView;
            }
            currentView = "/fxml/wishlist-view.fxml";

        } catch (IOException e) {
            logError("Failed to load friend's wishlist view", e);
            showError("Failed to load wishlist. Please try again.");
        }
    }

    public void navToSecretSanta() {
        loadView(AppRoutes.EVENTS_DASHBOARD);
    }

    public void navToEventDetails(SecretSantaEvent event) {
        User me = dataService.getLoggedInUser();

        boolean canManage = me.isAdmin() || event.getOwnerLogin().equals(me.getLogin());
        String fxml = canManage ? AppRoutes.ADMIN : AppRoutes.REVEAL;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();

            if (canManage) {
                AdminController controller = loader.getController();
                controller.setMainLayoutController(this);
                controller.setEvent(event);
            } else {
                RevealController controller = loader.getController();
                controller.setMainLayoutController(this);
                controller.setEvent(event);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            previousView = AppRoutes.EVENTS_DASHBOARD;
            currentView = fxml;
            currentEvent = event;

        } catch (IOException e) {
            logError("Failed to load event details view", e);
            showError("Failed to load event details. Please try again.");
        }
    }

    public void navToFeedback() {
        loadView(AppRoutes.FEEDBACK);
    }

    public void navToFriends() {
        loadView(AppRoutes.FRIENDS);
    }

    public void navToPreviousView() {
        if (!previousView.isEmpty()) {
            if ((AppRoutes.REVEAL.equals(previousView) || AppRoutes.ADMIN.equals(previousView))
                    && currentEvent != null) {
                navToEventDetails(currentEvent);
            } else {
                loadView(previousView);
            }
            previousView = "";
        }
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
            logError("Failed to load view: " + fxmlPath, e);
            showError("Failed to load view. Please try again.");
        }
    }
}

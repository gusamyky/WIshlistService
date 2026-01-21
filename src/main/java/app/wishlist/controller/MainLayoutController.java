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
    private SecretSantaEvent currentEvent = null; // Track the current event for Reveal/Admin views

    @FXML
    public void initialize() {
        // Trigger re-compilation
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

            // Save the current view before navigating to friend's wishlist
            // Check if we're coming from Reveal or Admin view
            if (AppRoutes.REVEAL.equals(currentView) || AppRoutes.ADMIN.equals(currentView)) {
                previousView = currentView;
            } else {
                previousView = currentView.isEmpty() ? AppRoutes.FRIENDS : currentView;
            }
            currentView = "/fxml/wishlist-view.fxml"; // Track that we're now in friend's wishlist

        } catch (IOException e) {
            e.printStackTrace();
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

            // Save previous view (events dashboard) for back navigation
            previousView = AppRoutes.EVENTS_DASHBOARD;
            currentView = fxml; // Track that we're now in Admin or Reveal view
            currentEvent = event; // Remember the event for potential back navigation

        } catch (IOException e) {
            e.printStackTrace();
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
            // Special handling for Reveal/Admin views - need to restore event data
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
            e.printStackTrace();
        }
    }
}

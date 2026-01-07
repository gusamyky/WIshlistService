package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.service.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class FriendsController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private ListView<User> friendsList;
    private MainLayoutController mainLayoutController; // To navigate

    // Setter to allow navigation back to the main layout logic
    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    @FXML
    public void initialize() {
        // 1. Load all users EXCEPT the current one
        User currentUser = dataService.getLoggedInUser();

        friendsList.getItems().addAll(
                dataService.getAllUsers().stream()
                        .filter(u -> !u.getLogin().equals(currentUser.getLogin())) // Filter out self
                        .toList()
        );

        // 2. Custom Cell Factory (Display Name + Login)
        friendsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getFullName() + " (@" + user.getLogin() + ")");
                }
            }
        });

        // 3. Handle Click
        friendsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedUser) -> {
            if (selectedUser != null && mainLayoutController != null) {
                // Navigate to that friend's wishlist
                mainLayoutController.navToFriendWishlist(selectedUser);
            }
        });
    }
}

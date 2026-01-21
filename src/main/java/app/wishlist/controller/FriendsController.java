package app.wishlist.controller;

import app.wishlist.model.domain.user.User;
import app.wishlist.service.impl.DataServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class FriendsController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
    private final ObservableList<User> displayedUsers = FXCollections.observableArrayList();
    @FXML
    private TextField searchField;
    @FXML
    private ListView<User> usersList;
    @FXML
    private Label listTitleLabel;
    @Setter
    private MainLayoutController mainLayoutController;

    @FXML
    public void initialize() {
        usersList.setItems(displayedUsers);

        usersList.setCellFactory(param -> new UserListCell());

        showMyFriends();

        initSearchField();
    }

    private void initSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                showMyFriends();
            } else {
                handleSearch();
            }
        });
    }

    private void showMyFriends() {
        listTitleLabel.setText("My Friends");

        User me = dataService.getLoggedInUser();

        if (me == null)
            return;

        List<User> friends = dataService.getAllUsers().stream()
                .filter(u -> dataService.isFriend(me, u.getLogin()))
                .collect(Collectors.toList());

        displayedUsers.setAll(friends);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            showMyFriends();
            return;
        }

        listTitleLabel.setText("Search Results");
        User me = dataService.getLoggedInUser();

        List<User> results = dataService.getAllUsers().stream()
                .filter(u -> !u.getLogin().equals(me.getLogin()))
                .filter(u -> u.getLogin().toLowerCase().contains(query) ||
                        u.getFullName().toLowerCase().contains(query))
                .collect(Collectors.toList());

        displayedUsers.setAll(results);
    }

    // --- INNER CLASS FOR CUSTOM CELLS ---
    private class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox container = new HBox(10);
                container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                // User Info
                Label nameLabel = new Label(user.getFullName() + " (@" + user.getLogin() + ")");
                nameLabel.getStyleClass().add("friend-name");

                // Spacer
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Action Button
                Button actionBtn = new Button();
                User me = dataService.getLoggedInUser();
                boolean isFriend = dataService.isFriend(me, user.getLogin());

                if (isFriend) {
                    actionBtn.setText("Remove");
                    actionBtn.getStyleClass().addAll("button-danger", "button-remove-friend");
                    actionBtn.setOnAction(e -> {
                        dataService.removeFriend(me, user.getLogin());
                        // Refresh logic based on current view
                        if (listTitleLabel.getText().equals("My Friends")) {
                            getListView().getItems().remove(user);
                        } else {
                            updateItem(user, false); // Refresh just this cell
                        }
                    });
                } else {
                    actionBtn.setText("Add Friend");
                    actionBtn.getStyleClass().add("button-success");
                    actionBtn.setOnAction(e -> {
                        dataService.addFriend(me, user.getLogin());
                        updateItem(user, false); // Refresh cell to show "Remove"
                    });
                }
                
                container.setOnMouseClicked(e -> {
                    if (mainLayoutController != null) {
                        mainLayoutController.navToFriendWishlist(user);
                    }
                });

                container.getChildren().addAll(nameLabel, spacer, actionBtn);
                setGraphic(container);
            }
        }
    }
}

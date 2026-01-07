package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.model.WishItem;
import app.wishlist.service.DataService;
import app.wishlist.view.WishItemCard;
import app.wishlist.viewmodel.WishItemViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class WishlistController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private Label pageTitle;
    @FXML
    private Label userLabel;
    @FXML
    private FlowPane itemsContainer;
    @FXML
    private Button addItemButton; // Make sure fx:id="addItemButton" is in your FXML
    private User targetUser; // The owner of the wishlist we are viewing

    @FXML
    public void initialize() {
        // Default to current logged-in user if setup() isn't called
        if (targetUser == null) {
            targetUser = dataService.getLoggedInUser();
        }
        refreshView();
    }

    // Call this from MainLayoutController to switch modes
    public void setup(User user) {
        this.targetUser = user;
        refreshView();
    }

    private void refreshView() {
        if (targetUser == null) return;

        // Always show who is currently logged in (for context)
        User currentUser = dataService.getLoggedInUser();
        if (currentUser != null) {
            userLabel.setText("(Logged in as " + currentUser.getLogin() + ")");
        }

        // Determine Mode
        boolean isOwner = isCurrentUserOwner();

        // Update the Main Title
        if (isOwner) {
            pageTitle.setText("My Wishlist");
            if (addItemButton != null) addItemButton.setVisible(true);
        } else {
            pageTitle.setText(targetUser.getFullName() + "'s Wishlist");
            if (addItemButton != null) addItemButton.setVisible(false);
        }

        loadWishlist(isOwner);
    }

    private boolean isCurrentUserOwner() {
        if (targetUser == null || dataService.getLoggedInUser() == null) return false;
        return targetUser.getLogin().equals(dataService.getLoggedInUser().getLogin());
    }

    private void loadWishlist(boolean isOwner) {
        itemsContainer.getChildren().clear();

        List<WishItem> items = dataService.getWishlistForUser(targetUser);

        for (WishItem item : items) {
            WishItemViewModel viewModel = new WishItemViewModel(item);

            if (isOwner) {
                // OWNER MODE: Edit & Delete, NO Reserve
                itemsContainer.getChildren().add(new WishItemCard(
                        viewModel,
                        this::handleEditItem,
                        this::handleDeleteItem,
                        null // No Reserve action for owner
                ));
            } else {
                // SHOPPING MODE: NO Edit/Delete, YES Reserve
                itemsContainer.getChildren().add(new WishItemCard(
                        viewModel,
                        null,
                        null,
                        this::handleReserveItem // New Action
                ));
            }
        }
    }

    // --- Actions ---

    @FXML
    private void handleAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item-dialog-view.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Wishlist Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(itemsContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ItemDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                WishItem newItem = controller.getResultItem();
                dataService.addWishItem(newItem);
                refreshView(); // Use refreshView() instead of calling loadWishlist() directly
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // New Action Handler
    private void handleReserveItem(WishItemViewModel viewModel) {
        // Toggle the reservation logic in the ViewModel
        // The ViewModel should handle the logic of "who reserved it"
        viewModel.toggleReservation(dataService.getLoggedInUser().getLogin());

        // Save change to DataService
        dataService.updateWishItem(viewModel.getModel());

        // Optional: Show a small confirmation or sound
        System.out.println("Item reservation status changed: " + viewModel.isReservedProperty().get());
    }

    private void handleEditItem(WishItemViewModel viewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item-dialog-view.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(itemsContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ItemDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setItem(viewModel.getModel());

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                WishItem newItem = controller.getResultItem();
                dataService.updateWishItem(newItem);
                refreshView(); // Use refreshView()
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteItem(WishItemViewModel viewModel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete " + viewModel.nameProperty().get() + "?");
        alert.setContentText("Are you sure? This cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dataService.removeWishItem(viewModel.getModel());
            refreshView(); // Use refreshView()
        }
    }
}

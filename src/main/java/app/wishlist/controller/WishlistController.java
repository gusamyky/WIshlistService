package app.wishlist.controller;

import app.wishlist.model.WishItem;
import app.wishlist.service.DataService;
import app.wishlist.view.WishItemCard;
import app.wishlist.viewmodel.WishItemViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class WishlistController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private Label userLabel;
    @FXML
    private FlowPane itemsContainer;

    @FXML
    public void initialize() {
        // Display current user
        if (dataService.getLoggedInUser() != null) {
            userLabel.setText("Logged in as: " + dataService.getLoggedInUser().getFullName());
        }

        loadWishlist();
    }

    private void loadWishlist() {
        itemsContainer.getChildren().clear();

        for (WishItem item : dataService.getCurrentUserWishlist()) {
            WishItemViewModel viewModel = new WishItemViewModel(item);

            // PASS THE ACTIONS HERE: (viewModel -> handleEdit(viewModel), viewModel -> handleDelete(viewModel))
            WishItemCard card = new WishItemCard(
                    viewModel,
                    this::handleEditItem,
                    this::handleDeleteItem
            );

            itemsContainer.getChildren().add(card);
        }
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

            // IMPORTANT: Pre-fill the dialog with data
            controller.setItem(viewModel.getModel());

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                WishItem newItem = controller.getResultItem();

                // NEW: Explicitly update the data in the service
                dataService.updateWishItem(newItem);

                // Refresh the UI
                loadWishlist();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleDeleteItem(WishItemViewModel viewModel) {
        // 1. Confirm with User
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete " + viewModel.nameProperty().get() + "?");
        alert.setContentText("Are you sure? This cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 2. Remove from DataService
            dataService.removeWishItem(viewModel.getModel());

            // 3. Refresh View
            loadWishlist();
        }
    }

    @FXML
    private void handleAddItem() {
        try {
            // 1. Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item-dialog-view.fxml"));
            Parent page = loader.load();

            // 2. Create the Stage (Popup Window)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Wishlist Item");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Blocks interaction with main window
            dialogStage.initOwner(itemsContainer.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. Pass the Stage to the Controller
            ItemDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // 4. Show and Wait
            dialogStage.showAndWait();

            // 5. Check result
            if (controller.isSaveClicked()) {
                WishItem newItem = controller.getResultItem();
                dataService.addWishItem(newItem);

                // Refresh the grid to show the new item
                loadWishlist();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error opening Item Dialog");
        }
    }
}

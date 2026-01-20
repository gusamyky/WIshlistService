package app.wishlist.controller;

import app.wishlist.consts.AppRoutes;
import app.wishlist.model.domain.user.User;
import app.wishlist.model.domain.wishlist.WishItem;
import app.wishlist.service.impl.DataServiceImpl;
import app.wishlist.view.components.WishItemCard;
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

public class WishlistController extends BaseController {

    private final DataServiceImpl dataService = DataServiceImpl.getInstance();
    @FXML
    private Label pageTitle;
    @FXML
    private Label userLabel;
    @FXML
    private FlowPane itemsContainer;
    @FXML
    private Button addItemButton;
    private User targetUser;

    @FXML
    public void initialize() {
        if (targetUser == null) {
            targetUser = dataService.getLoggedInUser();
        }

        refreshView();
    }

    public void setup(User user) {
        this.targetUser = user;
        refreshView();
    }

    private void refreshView() {
        if (targetUser == null)
            return;

        User currentUser = dataService.getLoggedInUser();
        if (currentUser != null) {
            userLabel.setText("(Logged in as " + currentUser.getLogin() + ")");
        }

        boolean isOwner = isCurrentUserOwner();

        if (isOwner) {
            pageTitle.setText("My Wishlist");
            if (addItemButton != null)
                addItemButton.setVisible(true);
        } else {
            pageTitle.setText(targetUser.getFullName() + "'s Wishlist");
            if (addItemButton != null)
                addItemButton.setVisible(false);
        }

        loadWishlist(isOwner);
    }

    private boolean isCurrentUserOwner() {
        if (targetUser == null || dataService.getLoggedInUser() == null)
            return false;
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
                boolean isReserved = viewModel.isReservedProperty().get();
                boolean isReservedByCurrentUser = isReserved &&
                        dataService.getLoggedInUser().getLogin().equals(item.getReservedByUserLogin());
                // SHOPPING MODE: NO Edit/Delete, YES Reserve
                if (isReservedByCurrentUser) {
                    // Item reserved by current user
                    itemsContainer.getChildren().add(new WishItemCard(
                            viewModel,
                            null,
                            null,
                            this::handleReserveItem));
                    continue;
                }

                // Item is either unreserved or reserved by current user
                itemsContainer.getChildren().add(new WishItemCard(
                        viewModel,
                        null,
                        null,
                        this::handleReserveItem));
            }
        }
    }

    // --- Actions ---

    @FXML
    private void handleAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppRoutes.ITEM_DIALOG));
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

                refreshView();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleReserveItem(WishItemViewModel viewModel) {

        viewModel.toggleReservation(dataService.getLoggedInUser().getLogin());

        dataService.updateWishItem(viewModel.getModel());
    }

    private void handleEditItem(WishItemViewModel viewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppRoutes.ITEM_DIALOG));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(itemsContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ItemDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.prefillItem(viewModel.getModel());

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                WishItem newItem = controller.getResultItem();
                dataService.updateWishItem(newItem);

                refreshView();
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

            refreshView();
        }
    }
}

package app.wishlist.controller;

import app.wishlist.service.DataService;
import app.wishlist.view.WishItemCard;
import app.wishlist.viewmodel.WishItemViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class WishlistController {

    private final DataService dataService = DataService.getInstance();
    @FXML
    private Label userLabel;
    @FXML
    private FlowPane itemsContainer;

    @FXML
    public void initialize() {
        // 1. Set User Info (Mock login)
        // In a real app, you'd get the current user from a UserContext or Session
        userLabel.setText("Logged in as: John Doe");

        // 2. Load Items
        loadWishlist();
    }

    private void loadWishlist() {
        itemsContainer.getChildren().clear();

        // Fetch mock data, wrap in ViewModel, create Card, add to View
        dataService.getCurrentUserWishlist().forEach(item -> {
            WishItemViewModel viewModel = new WishItemViewModel(item);
            WishItemCard card = new WishItemCard(viewModel);
            itemsContainer.getChildren().add(card);
        });
    }

    @FXML
    private void handleAddItem() {
        System.out.println("Open Add Item Dialog...");
        // Logic to open a dialog window goes here
    }
}

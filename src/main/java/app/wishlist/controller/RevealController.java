package app.wishlist.controller;

import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.service.SecretSantaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class RevealController {

    private final DataService dataService = DataService.getInstance();
    private final SecretSantaService santaService = SecretSantaService.getInstance();
    @FXML
    private VBox waitingBox;
    @FXML
    private VBox resultBox;
    @FXML
    private Label targetNameLabel;
    @Setter
    private MainLayoutController mainLayoutController;
    private User myTarget;

    @FXML
    public void initialize() {
        User me = dataService.getLoggedInUser();

        // 1. Check if Draw is Done
        if (!santaService.isDrawDone()) {
            showWaiting();
            return;
        }

        // 2. Find who I am buying for
        myTarget = santaService.getRecipientFor(me);

        if (myTarget != null) {
            showResult(myTarget);
        } else {
            // I might not be in the participation list
            waitingBox.setVisible(true);
            resultBox.setVisible(false);
            ((Label) waitingBox.getChildren().get(1)).setText("You are not participating in this draw.");
        }
    }

    private void showWaiting() {
        waitingBox.setVisible(true);
        resultBox.setVisible(false);
    }

    private void showResult(User target) {
        waitingBox.setVisible(false);
        resultBox.setVisible(true);
        targetNameLabel.setText(target.getFullName());
    }

    @FXML
    private void handleGoToWishlist() {
        if (mainLayoutController != null && myTarget != null) {
            mainLayoutController.navToFriendWishlist(myTarget);
        }
    }
}

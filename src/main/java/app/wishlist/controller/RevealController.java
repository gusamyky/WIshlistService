package app.wishlist.controller;

import app.wishlist.model.SecretSantaEvent;
import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.service.SecretSantaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class RevealController extends BaseController {

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
    private SecretSantaEvent currentEvent;
    private User myTarget;

    public void setEvent(SecretSantaEvent event) {
        this.currentEvent = event;
        loadData();
    }

    private void loadData() {
        if (currentEvent == null)
            return;

        User me = dataService.getLoggedInUser();

        if (!currentEvent.isDrawDone()) {
            showWaiting();
            return;
        }

        myTarget = santaService.getRecipientFor(currentEvent, me);

        if (myTarget != null) {
            showResult(myTarget);
        } else {
            waitingBox.setVisible(true);
            resultBox.setVisible(false);

            if (waitingBox.getChildren().size() > 1 && waitingBox.getChildren().get(1) instanceof Label) {
                ((Label) waitingBox.getChildren().get(1)).setText("You are not participating in this draw.");
            }
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

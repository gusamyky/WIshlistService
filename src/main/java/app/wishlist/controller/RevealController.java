package app.wishlist.controller;

import app.wishlist.model.SecretSantaEvent;
import app.wishlist.model.User;
import app.wishlist.service.DataService;
import app.wishlist.service.SecretSantaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RevealController {

    private final DataService dataService = DataService.getInstance();
    private final SecretSantaService santaService = SecretSantaService.getInstance();
    @FXML
    private VBox waitingBox;
    @FXML
    private VBox resultBox;
    @FXML
    private Label targetNameLabel;
    private MainLayoutController mainLayoutController;
    private SecretSantaEvent currentEvent; // <--- NEW: The specific event
    private User myTarget;

    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    // New Method: Called immediately after loading the view
    public void setEvent(SecretSantaEvent event) {
        this.currentEvent = event;
        loadData();
    }

    private void loadData() {
        if (currentEvent == null) return;

        User me = dataService.getLoggedInUser();

        // 1. Check if Draw is Done (Check the EVENT object, not the service)
        if (!currentEvent.isDrawDone()) {
            showWaiting();
            return;
        }

        // 2. Find who I am buying for (Pass the EVENT to the service)
        myTarget = santaService.getRecipientFor(currentEvent, me);

        if (myTarget != null) {
            showResult(myTarget);
        } else {
            waitingBox.setVisible(true);
            resultBox.setVisible(false);
            // Safety check in case they were removed or logic failed
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

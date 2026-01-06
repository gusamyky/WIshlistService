package app.wishlist.controller;

import app.wishlist.model.WishItem;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class ItemDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private TextArea descArea;

    // Call this to set the stage for closing later
    @Setter
    private Stage dialogStage;
    private WishItem resultItem;
    private boolean saveClicked = false;
    private String currentId = null;

    // Call this if editing an existing item (to pre-fill fields)
    public void setItem(WishItem item) {
        if (item != null) {
            this.currentId = item.getId();
            nameField.setText(item.getName());
            priceField.setText(String.valueOf(item.getPrice()));
            imageUrlField.setText(item.getImageUrl());
            descArea.setText(item.getDescription());
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public WishItem getResultItem() {
        return resultItem;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            // Build the item from fields
            resultItem = WishItem.builder()
                    .id(currentId)
                    .name(nameField.getText())
                    .description(descArea.getText())
                    .price(Double.parseDouble(priceField.getText()))
                    .imageUrl(imageUrlField.getText())
                    .isReserved(false) // Default for new items
                    .build();

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "No valid name!\n";
        }

        // Validate Price
        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Price must be a valid number!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Show Error Alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}

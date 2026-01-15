package app.wishlist.controller;

import app.wishlist.model.MonetaryAmount;
import app.wishlist.model.WishItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Currency;

public class ItemDialogController extends BaseController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private TextArea descArea;
    @FXML
    private ComboBox<Currency> currencyComboBox;

    @Setter
    private Stage dialogStage;
    @Getter
    private WishItem resultItem;
    @Getter
    private boolean saveClicked = false;
    private String currentId = null;
    private boolean isEditableItemReserved;

    @FXML
    public void initialize() {
        // Load currencies once when the view initializes
        currencyComboBox.getItems().addAll(MonetaryAmount.getAvailableCurrencies());

        // the default selection
        currencyComboBox.getSelectionModel().select(Currency.getInstance("PLN"));
    }

    public void prefillItem(WishItem item) {
        if (item != null) {
            // --- EDIT MODE ---
            this.currentId = item.getId();
            this.isEditableItemReserved = item.isReserved();

            nameField.setText(item.getName());
            imageUrlField.setText(item.getImageUrl());
            descArea.setText(item.getDescription());
            priceField.setText(String.valueOf(item.getPrice().getAmount()));
            currencyComboBox.getSelectionModel().select(item.getPrice().getCurrency());
        } else {
            // --- CREATE MODE ---
            this.currentId = null;
            nameField.clear();
            priceField.clear();
            imageUrlField.clear();
            descArea.clear();
            currencyComboBox.getSelectionModel().select(Currency.getInstance("PLN"));
        }
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            double amount = Double.parseDouble(priceField.getText());
            Currency selectedCurrency = currencyComboBox.getValue();

            var price = new MonetaryAmount(amount, selectedCurrency);

            resultItem = WishItem.builder()
                    .id(currentId)
                    .name(nameField.getText())
                    .description(descArea.getText())
                    .price(price)
                    .imageUrl(imageUrlField.getText())
                    .isReserved(currentId != null && isEditableItemReserved)
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

        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Price must be a valid number (use '.' for decimals)!\n";
        }

        if (currencyComboBox.getValue() == null) {
            errorMessage += "Please select a currency!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Invalid Fields", "Please correct invalid fields", errorMessage);
            return false;
        }
    }
}

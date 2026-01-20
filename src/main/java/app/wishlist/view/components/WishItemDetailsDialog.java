package app.wishlist.view.components;

import app.wishlist.viewmodel.WishItemViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class WishItemDetailsDialog extends Dialog<Void> {

    public WishItemDetailsDialog(WishItemViewModel viewModel) {
        this.setTitle("Wish Item Details");
        this.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        // Main Container
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);
        content.setPrefHeight(500);
        content.getStyleClass().add("dialog-content");

        // 1. Image (Larger)
        ImageView imageView = new ImageView();
        imageView.setFitHeight(200);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        imageView.imageProperty().bind(viewModel.imageProperty());
        HBox imageContainer = new HBox(imageView);
        imageContainer.setAlignment(Pos.CENTER);

        // 2. Name
        Label nameLabel = new Label();
        nameLabel.textProperty().bind(viewModel.nameProperty());
        nameLabel.getStyleClass().add("wish-details-name");
        nameLabel.setWrapText(true);

        // 3. Status & Price Row
        Label reservedLabel = new Label("RESERVED");
        reservedLabel.getStyleClass().add("badge-reserved");
        reservedLabel.visibleProperty().bind(viewModel.isReservedProperty());
        reservedLabel.managedProperty().bind(viewModel.isReservedProperty());

        Label priceLabel = new Label();
        priceLabel.textProperty().bind(viewModel.priceTextProperty());
        priceLabel.getStyleClass().add("wish-details-price");

        HBox metaRow = new HBox(15, priceLabel, reservedLabel);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        // 4. Description (TextArea for copy support)
        TextArea descArea = new TextArea();
        descArea.textProperty().bind(viewModel.descriptionProperty());
        descArea.setWrapText(true);
        descArea.setEditable(false);
        // Style to match the clean look: white background, black text
        descArea.getStyleClass().add("text-area-readonly");

        VBox.setVgrow(descArea, Priority.ALWAYS);

        // Assemble
        content.getChildren().addAll(imageContainer, nameLabel, metaRow, descArea);

        dialogPane.setContent(content);
    }
}

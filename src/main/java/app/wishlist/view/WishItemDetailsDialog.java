package app.wishlist.view;

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
        content.setStyle("-fx-background-color: white;");

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
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        nameLabel.setWrapText(true);

        // 3. Status & Price Row
        Label reservedLabel = new Label("RESERVED");
        reservedLabel.setStyle(
                "-fx-text-fill: white; -fx-background-color: #ff4444; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;");
        reservedLabel.visibleProperty().bind(viewModel.isReservedProperty());
        reservedLabel.managedProperty().bind(viewModel.isReservedProperty());

        Label priceLabel = new Label();
        priceLabel.textProperty().bind(viewModel.priceTextProperty());
        priceLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox metaRow = new HBox(15, priceLabel, reservedLabel);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        // 4. Description (TextArea for copy support)
        TextArea descArea = new TextArea();
        descArea.textProperty().bind(viewModel.descriptionProperty());
        descArea.setWrapText(true);
        descArea.setEditable(false);
        // Style to match the clean look: white background, black text
        descArea.setStyle(
                "-fx-control-inner-background: white; -fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        VBox.setVgrow(descArea, Priority.ALWAYS);

        // Assemble
        content.getChildren().addAll(imageContainer, nameLabel, metaRow, descArea);

        dialogPane.setContent(content);

        // Optional: Apply CSS stylesheet if available, or just rely on inline for now
        // as per other views
        // dialogPane.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }
}

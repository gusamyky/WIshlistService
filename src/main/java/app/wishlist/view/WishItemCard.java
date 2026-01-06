package app.wishlist.view;

import app.wishlist.viewmodel.WishItemViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class WishItemCard extends VBox {

    // Updated Constructor accepts actions
    public WishItemCard(WishItemViewModel viewModel,
                        Consumer<WishItemViewModel> onEdit,
                        Consumer<WishItemViewModel> onDelete) {

        // 1. Style the Card
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.getStyleClass().add("card");
        this.setPrefWidth(250);
        this.setMinWidth(250);
        this.setMaxWidth(250); // Keep cards uniform
        this.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 8;");

        // 2. Image View
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(true);
        imageView.imageProperty().bind(viewModel.imageProperty());

        // Center the image
        HBox imageContainer = new HBox(imageView);
        imageContainer.setAlignment(Pos.CENTER);

        // 3. Labels
        Label nameLabel = new Label();
        nameLabel.getStyleClass().add("title-4");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        nameLabel.textProperty().bind(viewModel.nameProperty());

        Label priceLabel = new Label();
        priceLabel.getStyleClass().add("accent");
        priceLabel.setStyle("-fx-text-fill: green;");
        priceLabel.textProperty().bind(viewModel.priceTextProperty());

        Label descLabel = new Label();
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60); // Limit height
        descLabel.textProperty().bind(viewModel.descriptionProperty());

        // 4. Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("button-outlined");
        // Trigger the passed 'onEdit' action
        editBtn.setOnAction(e -> onEdit.accept(viewModel));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().addAll("button-danger", "small");
        // Trigger the passed 'onDelete' action
        deleteBtn.setOnAction(e -> onDelete.accept(viewModel));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actions.getChildren().addAll(spacer, editBtn, deleteBtn);

        // 5. Assemble
        this.getChildren().addAll(imageContainer, nameLabel, priceLabel, descLabel, actions);

        // 6. Reservation Visual State
        viewModel.isReservedProperty().addListener((obs, oldVal, isReserved) -> {
            if (isReserved) {
                this.setOpacity(0.5);
                this.setDisable(true); // Disable buttons if reserved (optional rule)
            } else {
                this.setOpacity(1.0);
                this.setDisable(false);
            }
        });
    }
}

package app.wishlist.view;

import app.wishlist.viewmodel.WishItemViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class WishItemCard extends VBox {

    public WishItemCard(WishItemViewModel viewModel,
                        Consumer<WishItemViewModel> onEdit,
                        Consumer<WishItemViewModel> onDelete,
                        Consumer<WishItemViewModel> onReserve) {

        // 1. Base Style
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.getStyleClass().add("card");
        this.setPrefWidth(250);
        this.setMinWidth(250);
        this.setMaxWidth(250);

        // 2. Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(true);
        imageView.imageProperty().bind(viewModel.imageProperty());
        HBox imageContainer = new HBox(imageView);
        imageContainer.setAlignment(Pos.CENTER);

        // 3. Labels
        Label nameLabel = new Label();
        nameLabel.textProperty().bind(viewModel.nameProperty());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label priceLabel = new Label();
        priceLabel.textProperty().bind(viewModel.priceTextProperty());
        priceLabel.setStyle("-fx-text-fill: green;");

        Label descLabel = new Label();
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);
        descLabel.textProperty().bind(viewModel.descriptionProperty());

        // 4. Buttons Container
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().add(spacer);

        // --- BUTTON LOGIC START ---

        // CASE A: Shopping Mode (Reserve Button)
        if (onReserve != null) {
            ToggleButton reserveBtn = new ToggleButton("Reserve");
            reserveBtn.getStyleClass().add("button-accent");

            // Fix: No binding, manual sync to avoid conflict
            reserveBtn.setSelected(viewModel.isReservedProperty().get());

            reserveBtn.setOnAction(e -> onReserve.accept(viewModel));

            viewModel.isReservedProperty().addListener((obs, oldVal, newVal) -> {
                reserveBtn.setSelected(newVal);
            });

            actions.getChildren().add(reserveBtn);
        }
        // CASE B: Owner Mode (Edit & Delete Buttons)
        else {
            if (onEdit != null) {
                Button editBtn = new Button("Edit");
                editBtn.getStyleClass().add("button-outlined");
                editBtn.setOnAction(e -> onEdit.accept(viewModel));
                actions.getChildren().add(editBtn);
            }
            if (onDelete != null) {
                Button deleteBtn = new Button("Delete");
                deleteBtn.getStyleClass().addAll("button-danger", "small");
                deleteBtn.setOnAction(e -> onDelete.accept(viewModel));
                actions.getChildren().add(deleteBtn);
            }
        }
        // --- BUTTON LOGIC END ---

        this.getChildren().addAll(imageContainer, nameLabel, priceLabel, descLabel, actions);

        // 5. Visual State Listener
        viewModel.isReservedProperty().addListener((obs, oldVal, isReserved) -> {
            updateVisualState(isReserved);
        });

        // Run once to set initial state
        updateVisualState(viewModel.isReservedProperty().get());
    }

    private void updateVisualState(boolean isReserved) {
        if (isReserved) {
            this.setStyle("-fx-background-color: #e0e0e0; -fx-opacity: 0.7; -fx-background-radius: 8; -fx-border-color: #a0a0a0; -fx-border-radius: 8;");
        } else {
            this.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 8;");
        }
    }
}

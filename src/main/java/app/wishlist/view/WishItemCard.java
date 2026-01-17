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

        // --- RESERVED INDICATOR ---
        Label reservedLabel = new Label("RESERVED");
        reservedLabel.setStyle(
                "-fx-text-fill: white; -fx-background-color: #ff4444; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 2 6 2 6; -fx-background-radius: 4;");
        reservedLabel.setMinWidth(75);
        reservedLabel.setPrefWidth(75);
        reservedLabel.setMaxWidth(75);
        reservedLabel.setAlignment(Pos.CENTER);
        reservedLabel.visibleProperty().bind(viewModel.isReservedProperty());
        reservedLabel.managedProperty().bind(viewModel.isReservedProperty()); // Don't take space if hidden

        // Title Row
        HBox titleRow = new HBox(10, nameLabel, reservedLabel);
        titleRow.setAlignment(Pos.CENTER_LEFT);

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
            ToggleButton reserveBtn = new ToggleButton();
            reserveBtn.getStyleClass().add("button-accent");

            // Initial State Logic
            updateReserveButtonState(reserveBtn, viewModel);

            reserveBtn.setOnAction(e -> {
                // If it was disabled, this event technically shouldn't fire via UI, but good to
                // check
                if (!reserveBtn.isDisabled()) {
                    onReserve.accept(viewModel);
                }
            });

            // Listen for changes
            viewModel.isReservedProperty().addListener((obs, oldVal, newVal) -> {
                updateReserveButtonState(reserveBtn, viewModel);
            });
            viewModel.isReservedByCurrentUserProperty().addListener((obs, oldVal, newVal) -> {
                updateReserveButtonState(reserveBtn, viewModel);
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

        this.getChildren().addAll(imageContainer, titleRow, priceLabel, descLabel, actions);

        // 5. Visual State Listener
        viewModel.isReservedProperty().addListener((obs, oldVal, isReserved) -> {
            updateVisualState(isReserved);
        });

        // 6. Detailed View Listener
        this.setOnMouseClicked(event -> {
            new WishItemDetailsDialog(viewModel).showAndWait();
        });

        // Run once to set initial state
        updateVisualState(viewModel.isReservedProperty().get());
    }

    private void updateReserveButtonState(ToggleButton btn, WishItemViewModel vm) {
        boolean isReserved = vm.isReservedProperty().get();
        boolean isReservedByMe = vm.isReservedByCurrentUserProperty().get();

        if (isReserved) {
            if (isReservedByMe) {
                // Reserved by ME -> Enable "Unreserve"
                btn.setText("Unreserve");
                btn.setDisable(false);
                btn.setSelected(true); // Visually pressed usually means "Active/Reserved"
            } else {
                // Reserved by OTHERS -> Disable button
                btn.setText("Reserved"); // Or "Reserved by other"
                btn.setDisable(true);
                btn.setSelected(true);
            }
        } else {
            // Not Reserved -> Enable "Reserve"
            btn.setText("Reserve");
            btn.setDisable(false);
            btn.setSelected(false);
        }
    }

    private void updateVisualState(boolean isReserved) {
        // Requested: text says "reserved", but "colors of the whole tile are the same
        // as unreserved"
        // So we just keep the default white/shadow style, maybe just ensuring it
        // doesn't look disabled/gray.
        // The original code set background to #e0e0e0. We revert that.

        this.setStyle(
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 8;");
    }
}

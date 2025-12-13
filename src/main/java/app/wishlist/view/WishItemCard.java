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

public class WishItemCard extends VBox {

    public WishItemCard(WishItemViewModel viewModel) {
        // 1. Style the Card Container
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.getStyleClass().add("card"); // CSS class we will define later
        this.setPrefWidth(250);
        this.setMinWidth(250);

        // 2. Image View
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(true);
        // Bind the image property from ViewModel
        imageView.imageProperty().bind(viewModel.imageProperty());

        // 3. Labels
        Label nameLabel = new Label();
        nameLabel.getStyleClass().add("title-4");
        nameLabel.textProperty().bind(viewModel.nameProperty());

        Label priceLabel = new Label();
        priceLabel.getStyleClass().add("accent"); // Green color usually
        priceLabel.textProperty().bind(viewModel.priceTextProperty());

        Label descLabel = new Label();
        descLabel.setWrapText(true);
        descLabel.textProperty().bind(viewModel.descriptionProperty());

        // 4. Action Buttons (Edit/Delete)
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("button-outlined");

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().addAll("button-danger", "small");

        // Spacer to push buttons to right
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actions.getChildren().addAll(spacer, editBtn, deleteBtn);

        // 5. Assemble
        this.getChildren().addAll(imageView, nameLabel, priceLabel, descLabel, actions);

        // 6. Reservation Visual State (Grey out if reserved)
        viewModel.isReservedProperty().addListener((obs, oldVal, isReserved) -> {
            if (isReserved) {
                this.setOpacity(0.5);
                this.setStyle("-fx-background-color: #f0f0f0;"); // or styled via CSS
            } else {
                this.setOpacity(1.0);
                this.setStyle("");
            }
        });
    }
}

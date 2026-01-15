package app.wishlist.viewmodel;

import app.wishlist.model.WishItem;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import lombok.Getter;

import java.util.Objects;

public class WishItemViewModel {

    // The underlying data model
    @Getter
    private final WishItem model;

    // JavaFX Properties for Binding
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty priceText = new SimpleStringProperty();
    private final BooleanProperty isReserved = new SimpleBooleanProperty();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

    public WishItemViewModel(WishItem item) {
        this.model = item;

        // Initialize properties from Model
        name.set(item.getName());
        description.set(item.getDescription());
        priceText.set(String.format("%.2f " + item.getPrice().getCurrency().getSymbol(), item.getPrice().getAmount()));
        isReserved.set(item.isReserved());

        // Image Handling: Load or fallback
        try {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                image.set(new Image(item.getImageUrl(), true)); // background loading
            } else {
                // Load a placeholder from resources
                image.set(new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/icons/gift_placeholder.png"))));
            }
        } catch (Exception e) {
            // Handle error silently or set fallback
        }
    }

    // --- Property Getters for FXML Binding ---

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty priceTextProperty() {
        return priceText;
    }

    public BooleanProperty isReservedProperty() {
        return isReserved;
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    // --- Actions (Business Logic) ---

    public void toggleReservation(String currentUserLogin) {
        if (isReserved.get()) {
            // Logic to Un-reserve (only if current user is the one who reserved it)
            if (currentUserLogin.equals(model.getReservedByUserLogin())) {
                model.setReserved(false);
                model.setReservedByUserLogin(null);
                isReserved.set(false);
            }
        } else {
            // Logic to Reserve
            model.setReserved(true);
            model.setReservedByUserLogin(currentUserLogin);
            isReserved.set(true);
        }
        // In a real app, you would call a Service here to save to DB
    }

}

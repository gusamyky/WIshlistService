package app.wishlist;

import app.wishlist.view.ViewSwitcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // 1. Setup Root Pane
        StackPane root = new StackPane();
        ViewSwitcher.setScene(new Scene(root, 900, 600)); // Default size 900x600
        ViewSwitcher.setRoot(root);

        // 2. Load Global CSS
        ViewSwitcher.getScene().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        // 3. Configure Stage (Window)
        stage.setScene(ViewSwitcher.getScene());
        stage.setTitle("Secret Santa & Wishlist Manager");

        // Requirement: Min/Max sizes
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        // stage.setMaxWidth(1200); // Optional: usually better to allow full maximize

        // Requirement: App Icon
        // Ensure you have an image at src/main/resources/images/icon.png
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        } catch (Exception e) {
            System.out.println("Icon not found, using default.");
        }

        stage.show();

        // 4. Initial View
        ViewSwitcher.switchTo(ViewSwitcher.LOGIN);
    }
}

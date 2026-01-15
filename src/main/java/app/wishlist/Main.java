package app.wishlist;

import app.wishlist.consts.AppRoutes;
import app.wishlist.view.ViewSwitcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        ViewSwitcher.setScene(new Scene(root, 900, 600)); // Default size 900x600
        ViewSwitcher.setRoot(root);

        ViewSwitcher.getScene().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        stage.setScene(ViewSwitcher.getScene());
        stage.setTitle("Wishlist Service");

        stage.setMinWidth(800);
        stage.setMinHeight(500);


        // Set a custom icon, with fallback
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/wish-list-icon.png"))));
        } catch (Exception e) {
            System.out.println("Icon not found, using default.");
        }

        // Set Dock Icon for macOS
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage(getClass().getResource("/icons/wish-list-icon.png"));
                taskbar.setIconImage(dockIcon);
            }

        }

        stage.show();

        ViewSwitcher.switchTo(AppRoutes.LOGIN);
    }
}

package app.wishlist.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@UtilityClass
public class ViewSwitcher {
    private static final Logger logger = LoggerFactory.getLogger(ViewSwitcher.class);

    @Getter
    private static Scene scene;
    @Getter
    private static Pane root;

    public static void setScene(Scene scene) {
        ViewSwitcher.scene = scene;
    }

    public static void setRoot(Pane root) {
        ViewSwitcher.root = root;
    }

    public static void switchTo(String fxmlPath) {
        try {
            Parent view;

            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource(fxmlPath));
            view = loader.load();

            if (root != null) {
                root.getChildren().clear();
                root.getChildren().add(view);
            } else {
                if (scene != null) {
                    scene.setRoot(view);
                }
            }

        } catch (IOException e) {
            logger.error("Failed to load view: {}", fxmlPath, e);
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }
}

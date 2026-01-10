package app.wishlist.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewSwitcher {

    // File Constants
    public static final String LOGIN = "/fxml/login-view.fxml";
    public static final String REGISTER = "/fxml/register-view.fxml";
    public static final String MAIN_LAYOUT = "/fxml/main-layout.fxml";
    // Cache for views (Optional optimization, good for performance)
    private static final Map<String, Parent> cache = new HashMap<>();
    // Global references
    private static Scene scene;
    private static Pane root; // Usually the StackPane from Main.java

    // --- MISSING METHODS ADDED HERE ---

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        ViewSwitcher.scene = scene;
    }

    public static Pane getRoot() {
        return root;
    }

    public static void setRoot(Pane root) {
        ViewSwitcher.root = root;
    }

    // --- NAVIGATION LOGIC ---

    public static void switchTo(String fxmlPath) {
        try {
            Parent view;

            // (Optional) Simple caching strategy:
            // if (cache.containsKey(fxmlPath)) {
            //    view = cache.get(fxmlPath);
            // } else {
            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource(fxmlPath));
            view = loader.load();
            //    cache.put(fxmlPath, view);
            // }

            if (root != null) {
                // We are swapping the content inside the root StackPane
                root.getChildren().clear();
                root.getChildren().add(view);
            } else {
                // Fallback: If root isn't set, replace the entire Scene root
                // (Useful if you change architecture later)
                if (scene != null) {
                    scene.setRoot(view);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }
}

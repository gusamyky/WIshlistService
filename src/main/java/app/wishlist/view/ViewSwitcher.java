package app.wishlist.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewSwitcher {

    public static final String LOGIN = "/fxml/login-view.fxml";
    public static final String MAIN_LAYOUT = "/fxml/main-layout.fxml"; // We will build this next

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource(fxmlPath));
            Parent root = loader.load();

            // If the scene already exists, just replace the root (smoother)
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 900, 600));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }
}

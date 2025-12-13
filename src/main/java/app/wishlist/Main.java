package app.wishlist;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Apply AtlantaFX Theme (Modern Look)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/wishlist-view.fxml"));

        // Create Scene
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Show Stage
        stage.setTitle("Wishlist Service");

        // Set the min window size
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        stage.setScene(scene);
        stage.show();
    }
}

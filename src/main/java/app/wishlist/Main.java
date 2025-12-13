package app.wishlist;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Create a root node (container)
        StackPane root = new StackPane();

        // 2. Add something to it (optional, just so it's not empty)
        root.getChildren().add(new Label("Hello JavaFX!"));

        // 3. Set up the stage
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
}

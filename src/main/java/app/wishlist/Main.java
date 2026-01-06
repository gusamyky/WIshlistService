package app.wishlist;

import app.wishlist.view.ViewSwitcher;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // 1. Theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // 2. Setup Switcher
        ViewSwitcher.setStage(stage);
        stage.setTitle("Wishlist Service");

        // 3. Start at Login
        ViewSwitcher.switchTo(ViewSwitcher.LOGIN);
    }
}

module app.wishlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires atlantafx.base;
    requires com.google.gson; // ADD THIS
    requires transitive javafx.graphics;

    opens app.wishlist to javafx.fxml;

    exports app.wishlist;

    // EXPORT/OPEN PACKAGES FOR FXML AND GSON
    exports app.wishlist.controller;

    opens app.wishlist.controller to javafx.fxml;

    exports app.wishlist.model;

    opens app.wishlist.model to com.google.gson; // IMPORTANT: Allow Gson to see models

    exports app.wishlist.service; // Optional

    opens app.wishlist.service to com.google.gson;
}

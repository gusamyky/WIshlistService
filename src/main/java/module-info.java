module app.wishlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires atlantafx.base;
    requires com.google.gson;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens app.wishlist to javafx.fxml;

    exports app.wishlist;

    // EXPORT/OPEN PACKAGES FOR FXML AND GSON
    exports app.wishlist.controller;
    exports app.wishlist.consts;

    // Model domain packages
    exports app.wishlist.model.domain.user;
    exports app.wishlist.model.domain.wishlist;
    exports app.wishlist.model.domain.event;
    exports app.wishlist.model.valueobject;
    exports app.wishlist.model.report;

    // Service packages (existing)
    exports app.wishlist.service.interfaces;

    // View packages
    exports app.wishlist.view;
    exports app.wishlist.view.components;

    // ViewModel packages
    exports app.wishlist.viewmodel;

    opens app.wishlist.controller to javafx.fxml;

    // Open model packages to Gson
    opens app.wishlist.model.domain.user to com.google.gson;
    opens app.wishlist.model.domain.wishlist to com.google.gson;
    opens app.wishlist.model.domain.event to com.google.gson;
    opens app.wishlist.model.valueobject to com.google.gson;

    // Open service implementations to Gson (for DataWrapper and event persistence)
    opens app.wishlist.service.impl to com.google.gson;
}

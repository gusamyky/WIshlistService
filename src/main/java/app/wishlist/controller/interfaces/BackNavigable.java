package app.wishlist.controller.interfaces;

/**
 * Interface for controllers that support back navigation.
 * Controllers implementing this interface must provide a handleBack() method
 * that defines their specific back navigation behavior.
 */
public interface BackNavigable {

    /**
     * Handles the back navigation action.
     * Each implementing controller defines where "back" goes.
     */
    void navigateBack();
}

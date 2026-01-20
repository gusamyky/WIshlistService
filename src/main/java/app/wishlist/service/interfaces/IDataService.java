package app.wishlist.service.interfaces;

import app.wishlist.model.domain.user.User;
import app.wishlist.model.domain.wishlist.WishItem;

import java.util.List;

/**
 * Interface for data service operations handling user management,
 * authentication, friends, and wishlist management.
 */
public interface IDataService {

    // Authentication
    boolean authenticate(String login, String inputPassword);

    User getLoggedInUser();

    void setLoggedInUser(User user);

    void logout();

    // User Management
    User getUserByLogin(String login);

    List<User> getAllUsers();

    boolean registerUser(String login, String password, String firstName, String lastName, boolean isAdmin);

    // Friend Management
    void addFriend(User me, String friendLogin);

    void removeFriend(User me, String friendLogin);

    boolean isFriend(User me, String otherLogin);

    // Wishlist Operations
    List<WishItem> getWishlistForUser(User user);

    List<WishItem> getCurrentUserWishlist();

    void addWishItem(WishItem item);

    void removeWishItem(WishItem item);

    void updateWishItem(WishItem newItem);

    // Persistence
    void saveData();
}

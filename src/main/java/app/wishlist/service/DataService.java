package app.wishlist.service;

import app.wishlist.model.User;
import app.wishlist.model.WishItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataService {

    // Singleton pattern for simplicity
    private static final DataService INSTANCE = new DataService();
    private final ObjectProperty<User> loggedInUser = new SimpleObjectProperty<>();
    private List<User> users = new ArrayList<>();
    private List<WishItem> currentUserItems = new ArrayList<>();

    private DataService() {
        seedData();
    }

    public static DataService getInstance() {
        return INSTANCE;
    }

    private void seedData() {
        // Create Users
        users.add(new User("jdoe", "123", "John", "Doe"));
        users.add(new User("asmith", "123", "Alice", "Smith"));

        // Create Items
        currentUserItems.add(WishItem.builder()
                .id(UUID.randomUUID().toString())
                .name("Gaming Mouse")
                .description("Wireless, RGB, High DPI")
                .price(59.99)
                .imageUrl("https://prod-api.mediaexpert.pl/api/images/gallery_500_500/thumbnails/images/57/5733968/Mysz-LOGITECH-G-PRO-X-Superlight-2-Lightspeed-Bialy-front-bok-lewy.jpeg")
                .isReserved(false)
                .build());

        currentUserItems.add(WishItem.builder()
                .id(UUID.randomUUID().toString())
                .name("Coffee Maker")
                .description("Pour over kit")
                .price(25.50)
                .imageUrl("https://target.scene7.com/is/image/Target/GUEST_57e2d054-fd81-4dac-84d7-3a1fffaa0582")
                .isReserved(true)
                .reservedByUserLogin("asmith")
                .build());
    }

    public List<User> getAllUsers() {
        return users;
    }

    public ObjectProperty<User> loggedInUserProperty() {
        return loggedInUser;
    }

    public User getLoggedInUser() {
        return loggedInUser.get();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser.set(user);
        // In a real app, you would fetch that user's specific items here
        System.out.println("User logged in: " + (user != null ? user.getLogin() : "null"));
    }

    public void logout() {
        setLoggedInUser(null);
    }

    public List<WishItem> getCurrentUserWishlist() {
        return currentUserItems;
    }

    public User login(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);
    }

    public void updateWishItem(WishItem newItem) {
        for (int i = 0; i < currentUserItems.size(); i++) {
            WishItem existing = currentUserItems.get(i);
            if (existing.getId() != null && existing.getId().equals(newItem.getId())) {
                currentUserItems.set(i, newItem); // Replace old with new
                return;
            }
        }
    }

    public void addWishItem(WishItem item) {
        // Ensure it has an ID
        if (item.getId() == null) {
            item.setId(UUID.randomUUID().toString());
        }
        currentUserItems.add(item);
        // In a real app, you would save this to the User's specific list
    }

    public void removeWishItem(WishItem item) {
        currentUserItems.removeIf(i -> i.getId().equals(item.getId()));
    }
}

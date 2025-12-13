package app.wishlist.service;

import app.wishlist.model.User;
import app.wishlist.model.WishItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataService {

    // Singleton pattern for simplicity
    private static final DataService INSTANCE = new DataService();
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

    public List<WishItem> getCurrentUserWishlist() {
        return currentUserItems;
    }

    public User login(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);
    }
}

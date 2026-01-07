package app.wishlist.service;

import app.wishlist.model.User;
import app.wishlist.model.WishItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;

public class DataService {

    private static final DataService INSTANCE = new DataService();

    // 1. Storage: Map User Login -> List of Items
    private final Map<String, List<WishItem>> wishlists = new HashMap<>();
    private final List<User> users = new ArrayList<>();

    private final ObjectProperty<User> loggedInUser = new SimpleObjectProperty<>();

    private DataService() {
        seedData();
    }

    public static DataService getInstance() {
        return INSTANCE;
    }

    // --- Auth ---
    public ObjectProperty<User> loggedInUserProperty() {
        return loggedInUser;
    }

    public User getLoggedInUser() {
        return loggedInUser.get();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser.set(user);
    }

    public void logout() {
        setLoggedInUser(null);
    }

    public List<User> getAllUsers() {
        return users;
    }

    // --- Wishlist Logic ---

    // Get the list for a SPECIFIC user (persistent)
    public List<WishItem> getWishlistForUser(User user) {
        // Ensure the list exists, if not create empty
        return wishlists.computeIfAbsent(user.getLogin(), k -> new ArrayList<>());
    }

    // Get CURRENT user's list (helper)
    public List<WishItem> getCurrentUserWishlist() {
        if (getLoggedInUser() == null) return new ArrayList<>();
        return getWishlistForUser(getLoggedInUser());
    }

    public void addWishItem(WishItem item) {
        if (getLoggedInUser() == null) return;

        if (item.getId() == null) {
            item.setId(UUID.randomUUID().toString());
        }
        // Add to the CURRENT user's mapped list
        getWishlistForUser(getLoggedInUser()).add(item);
    }

    public void removeWishItem(WishItem item) {
        if (getLoggedInUser() == null) return;
        getWishlistForUser(getLoggedInUser()).removeIf(i -> i.getId().equals(item.getId()));
    }

    public void updateWishItem(WishItem newItem) {
        // We need to find WHO owns this item to update it correctly.
        // For simplicity in this prototype, we will search all lists.

        for (List<WishItem> list : wishlists.values()) {
            for (int i = 0; i < list.size(); i++) {
                WishItem existing = list.get(i);
                if (existing.getId() != null && existing.getId().equals(newItem.getId())) {
                    list.set(i, newItem); // Update found item
                    return;
                }
            }
        }
    }

    // --- Mock Data Setup ---
    private void seedData() {
        // 1. Create Users
        User john = new User("jdoe", "123", "John", "Doe");
        User alice = new User("asmith", "123", "Alice", "Smith");
        User bob = new User("bjones", "123", "Bob", "Jones");

        users.addAll(List.of(john, alice, bob));

        // 2. Create Distinct Wishlists

        // John's List (Tech Stuff)
        List<WishItem> johnsList = new ArrayList<>();
        johnsList.add(WishItem.builder().id("1").name("Gaming Mouse").price(60.0).description("Logitech G502").imageUrl("").isReserved(false).build());
        johnsList.add(WishItem.builder().id("2").name("Mechanical Keyboard").price(120.0).description("Cherry MX Red").imageUrl("").isReserved(false).build());
        wishlists.put(john.getLogin(), johnsList);

        // Alice's List (Books & Coffee)
        List<WishItem> alicesList = new ArrayList<>();
        alicesList.add(WishItem.builder().id("3").name("Coffee Grinder").price(45.0).description("Burr grinder for espresso").imageUrl("").isReserved(false).build());
        alicesList.add(WishItem.builder().id("4").name("Fantasy Novel Set").price(30.0).description("Lord of the Rings Box Set").imageUrl("").isReserved(false).build());
        alicesList.add(WishItem.builder().id("5").name("Yoga Mat").price(20.0).description("Thick foam mat").imageUrl("").isReserved(false).build());
        wishlists.put(alice.getLogin(), alicesList);

        // Bob's List (Empty or random)
        List<WishItem> bobsList = new ArrayList<>();
        bobsList.add(WishItem.builder().id("6").name("Running Shoes").price(80.0).description("Size 42").imageUrl("").isReserved(false).build());
        wishlists.put(bob.getLogin(), bobsList);
    }
}

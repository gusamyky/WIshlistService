package app.wishlist.service;

import app.wishlist.model.User;
import app.wishlist.model.WishItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class DataService {

    private static final DataService INSTANCE = new DataService();
    private static final String DATA_FILE = "wishlist_data.json";
    private final Gson gson;
    private final ObjectProperty<User> loggedInUser = new SimpleObjectProperty<>();
    // Data Structures
    private List<User> users = new ArrayList<>();
    private Map<String, List<WishItem>> wishlists = new HashMap<>();

    private DataService() {
        // Configure Gson to be readable (Pretty Printing)
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadData();
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

    public boolean registerUser(String login, String password, String firstName, String lastName) {
        // 1. Check if login exists
        boolean exists = users.stream().anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
        if (exists) {
            return false; // Login taken
        }

        // 2. Create and Add
        User newUser = new User(login, password, firstName, lastName);
        users.add(newUser);

        // 3. Create an empty wishlist for them immediately
        wishlists.put(login, new ArrayList<>());

        // 4. Save to file
        saveData();
        return true;
    }

    // --- Wishlist Logic ---

    public List<WishItem> getWishlistForUser(User user) {
        return wishlists.computeIfAbsent(user.getLogin(), k -> new ArrayList<>());
    }

    public List<WishItem> getCurrentUserWishlist() {
        if (getLoggedInUser() == null) return new ArrayList<>();
        return getWishlistForUser(getLoggedInUser());
    }

    public void addWishItem(WishItem item) {
        if (getLoggedInUser() == null) return;
        if (item.getId() == null) item.setId(UUID.randomUUID().toString());

        getWishlistForUser(getLoggedInUser()).add(item);
        saveData(); // <--- SAVE
    }

    public void removeWishItem(WishItem item) {
        if (getLoggedInUser() == null) return;
        getWishlistForUser(getLoggedInUser()).removeIf(i -> i.getId().equals(item.getId()));
        saveData(); // <--- SAVE
    }

    public void updateWishItem(WishItem newItem) {
        // Search all lists to find the item and update it
        for (List<WishItem> list : wishlists.values()) {
            for (int i = 0; i < list.size(); i++) {
                WishItem existing = list.get(i);
                if (existing.getId() != null && existing.getId().equals(newItem.getId())) {
                    list.set(i, newItem);
                    saveData(); // <--- SAVE
                    return;
                }
            }
        }
    }

    // --- Persistence Logic ---

    private void saveData() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            DataWrapper wrapper = new DataWrapper();
            wrapper.users = this.users;
            wrapper.wishlists = this.wishlists;
            gson.toJson(wrapper, writer);
            System.out.println("Data saved to " + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            // If no file exists, create default/seed data for the first run
            seedData();
            saveData();
            return;
        }

        try (Reader reader = new FileReader(DATA_FILE)) {
            Type type = new TypeToken<DataWrapper>() {
            }.getType();
            DataWrapper wrapper = gson.fromJson(reader, type);

            if (wrapper != null) {
                this.users = wrapper.users != null ? wrapper.users : new ArrayList<>();
                this.wishlists = wrapper.wishlists != null ? wrapper.wishlists : new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void seedData() {
        System.out.println("Seeding initial data...");
        // This only runs ONCE if the file doesn't exist
        User john = new User("jdoe", "123", "John", "Doe");
        User alice = new User("asmith", "123", "Alice", "Smith");
        User bob = new User("bjones", "123", "Bob", "Jones");
        users.addAll(List.of(john, alice, bob));

        // Add sample items...
        List<WishItem> johnsList = new ArrayList<>();
        johnsList.add(WishItem.builder().id("1").name("Gaming Mouse").price(60.0).description("Logitech G502").isReserved(false).build());
        wishlists.put("jdoe", johnsList);
    }

    // A wrapper class to help JSON serialization
    private static class DataWrapper {
        List<User> users;
        Map<String, List<WishItem>> wishlists;
    }
}

package app.wishlist.service;

import app.wishlist.model.AdminUser;
import app.wishlist.model.User;
import app.wishlist.model.WishItem;
import com.google.gson.*;
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
    private List<User> users = new ArrayList<>();
    private Map<String, List<WishItem>> wishlists = new HashMap<>();

    private DataService() {
        // --- REQUIREMENT: Inner Class / Anonymous Class ---
        JsonDeserializer<User> userDeserializer = new JsonDeserializer<User>() {
            @Override
            public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : "USER";

                if ("ADMIN".equals(type)) {
                    // This is safe because AdminUser.class is DIFFERENT from User.class
                    // so it won't trigger this deserializer again.
                    return context.deserialize(jsonObject, AdminUser.class);
                }

                // --- FIX IS HERE ---
                // We cannot use context.deserialize(json, User.class) because it triggers THIS adapter again (Loop).
                // Instead, we use a temporary default Gson instance to parse the concrete User class.
                return new Gson().fromJson(jsonObject, User.class);
            }
        };

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(User.class, userDeserializer) // Register the adapter
                .create();

        loadData();
    }

    public static DataService getInstance() {
        return INSTANCE;
    }

    // --- Auth ---
    public ObjectProperty<User> loggedInUserProperty() {
        return loggedInUser;
    }

    public boolean authenticate(String login, String inputPassword) {
        // 1. Find user by login
        User user = users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);

        if (user == null) return false;

        // 2. Get the stored "Salt:Hash" string
        String storedValue = user.getPassword();

        // 3. Split it to recover the ORIGINAL Salt
        String[] parts = storedValue.split(":");

        // Safety check: ensure data isn't corrupted
        if (parts.length != 2) {
            System.err.println("Error: Stored password format is invalid for user " + login);
            return false;
        }

        String storedSaltString = parts[0];
        String storedHash = parts[1];

        // 4. Decode the Salt back to bytes
        byte[] originalSalt = Base64.getDecoder().decode(storedSaltString);

        // 5. Hash the input password using the ORIGINAL Salt
        String newHash = PasswordHashingService.hashPassword(inputPassword, originalSalt);

        // Debugging print (Optional - remove after fixing)
        System.out.println("Stored: " + storedHash);
        System.out.println("Calc'd: " + newHash);

        // 6. Compare
        return newHash.equals(storedHash);
    }

    // Also, update helper to get user object by login if needed
    public User getUserByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);
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

    // --- UPDATED REGISTRATION ---
    // We need to allow registering an Admin (for testing) or regular user
    public boolean registerUser(String login, String password, String firstName, String lastName, boolean isAdmin) {
        boolean exists = users.stream().anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
        if (exists) return false;

        // 1. Generate new Salt
        byte[] salt = PasswordHashingService.getSalt();

        // 2. Hash password with that Salt
        String hash = PasswordHashingService.hashPassword(password, salt);

        // 3. Combine Salt + Hash (Format: "salt:hash")
        String saltString = Base64.getEncoder().encodeToString(salt);
        String storedPassword = saltString + ":" + hash;

        // 4. Save User
        User newUser = new User(login, storedPassword, firstName, lastName);
        users.add(newUser);
        wishlists.put(login, new ArrayList<>());
        saveData();

        return true;
    }

    // --- Friends Logic ---

    public void addFriend(User me, String friendLogin) {
        if (me == null || friendLogin == null) return;
        // Logic: Add friend ID to my list
        me.getFriends().add(friendLogin);
        saveData(); // Persist changes
    }

    public void removeFriend(User me, String friendLogin) {
        if (me == null || friendLogin == null) return;
        me.getFriends().remove(friendLogin);
        saveData();
    }

    // Helper to check if someone is already a friend
    public boolean isFriend(User me, String otherLogin) {
        if (me == null) return false;
        return me.getFriends().contains(otherLogin);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            seedData(); // You might want to seed a default Admin here
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
        // Create a default Admin
        registerUser("admin", "admin", "System", "Admin", true);
    }

    private static class DataWrapper {
        List<User> users;
        Map<String, List<WishItem>> wishlists;
    }
}

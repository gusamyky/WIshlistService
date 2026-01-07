package app.wishlist.service;

import app.wishlist.model.SecretSantaUsersPair;
import app.wishlist.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecretSantaService {

    private static final SecretSantaService INSTANCE = new SecretSantaService();
    private static final String SANTA_FILE = "santa_draw.json";
    private final Gson gson;

    private LocalDate eventDate;
    private List<SecretSantaUsersPair> drawResults = new ArrayList<>();
    private boolean isDrawDone = false;

    private SecretSantaService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadState(); // Load on startup
    }

    public static SecretSantaService getInstance() {
        return INSTANCE;
    }

    public boolean isDrawDone() {
        return isDrawDone;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate date) {
        this.eventDate = date;
    }

    public void performDraw(List<User> participants) {
        if (participants == null || participants.size() < 2)
            throw new IllegalArgumentException("Need at least 2 participants!");

        List<User> givers = new ArrayList<>(participants);
        Collections.shuffle(givers);

        drawResults.clear();
        for (int i = 0; i < givers.size(); i++) {
            User santa = givers.get(i);
            User recipient = givers.get((i + 1) % givers.size());
            drawResults.add(new SecretSantaUsersPair(santa, recipient));
        }

        isDrawDone = true;
        saveState(); // <--- SAVE
    }

    public User getRecipientFor(User santa) {
        if (!isDrawDone) return null;
        return drawResults.stream()
                .filter(pair -> pair.getSanta().getLogin().equals(santa.getLogin()))
                .map(SecretSantaUsersPair::getRecipient)
                .findFirst()
                .orElse(null);
    }

    // --- Persistence ---

    private void saveState() {
        try (Writer writer = new FileWriter(SANTA_FILE)) {
            SantaStateWrapper wrapper = new SantaStateWrapper();
            wrapper.pairs = this.drawResults;
            wrapper.isDone = this.isDrawDone;
            if (this.eventDate != null) wrapper.eventDate = this.eventDate.toString();

            gson.toJson(wrapper, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadState() {
        File file = new File(SANTA_FILE);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(SANTA_FILE)) {
            SantaStateWrapper wrapper = gson.fromJson(reader, SantaStateWrapper.class);
            if (wrapper != null) {
                this.drawResults = wrapper.pairs != null ? wrapper.pairs : new ArrayList<>();
                this.isDrawDone = wrapper.isDone;
                if (wrapper.eventDate != null) {
                    this.eventDate = LocalDate.parse(wrapper.eventDate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SantaStateWrapper {
        List<SecretSantaUsersPair> pairs;
        String eventDate; // Store date as String for simplicity in JSON
        boolean isDone;
    }
}

package app.wishlist.service.impl;

import app.wishlist.model.domain.event.SecretSantaEvent;
import app.wishlist.model.domain.event.SecretSantaUsersPair;
import app.wishlist.model.domain.user.User;
import app.wishlist.service.interfaces.ISecretSantaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SecretSantaServiceImpl implements ISecretSantaService {

    private static final Logger logger = LoggerFactory.getLogger(SecretSantaServiceImpl.class);
    private static final SecretSantaServiceImpl INSTANCE = new SecretSantaServiceImpl();
    private static final String EVENTS_FILE = "events_data.json";
    private final Gson gson;

    private List<SecretSantaEvent> events = new ArrayList<>();

    private SecretSantaServiceImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadEvents();
    }

    public static SecretSantaServiceImpl getInstance() {
        return INSTANCE;
    }

    // --- Event Management ---

    public void createEvent(String name, User owner, LocalDate date) {
        SecretSantaEvent event = new SecretSantaEvent(name, owner.getLogin(), date);
        events.add(event);
        saveEvents();
    }

    public List<SecretSantaEvent> getMyEvents(User user) {
        if (user == null)
            return new ArrayList<>();

        if (user.isAdmin()) {
            return events; // Admins see all events
        }

        // Return created by or participating in events
        return events.stream()
                .filter(e -> e.getOwnerLogin().equals(user.getLogin())
                        || e.getParticipantLogins().contains(user.getLogin()))
                .collect(Collectors.toList());
    }

    public void addParticipant(SecretSantaEvent event, User user) {
        if (!event.getParticipantLogins().contains(user.getLogin())) {
            event.getParticipantLogins().add(user.getLogin());
            saveEvents();
        }
    }

    public void removeParticipant(SecretSantaEvent event, User user) {
        event.getParticipantLogins().remove(user.getLogin());
        saveEvents();
    }

    // --- The Draw Algorithm ---
    public void performDraw(SecretSantaEvent event) {
        List<String> participants = new ArrayList<>(event.getParticipantLogins());

        if (participants.size() < 4) {
            throw new IllegalStateException("Minimum 4 participants required!");
        }

        Collections.shuffle(participants);
        event.getDrawResults().clear();
        DataServiceImpl data = DataServiceImpl.getInstance();

        for (int i = 0; i < participants.size(); i++) {
            String santaLogin = participants.get(i);
            String targetLogin = participants.get((i + 1) % participants.size());

            User santa = data.getUserByLogin(santaLogin);
            User target = data.getUserByLogin(targetLogin);

            if (santa != null && target != null) {
                event.getDrawResults().add(new SecretSantaUsersPair(santa, target));
            }
        }

        event.setDrawDone(true);
        saveEvents();
    }

    // --- Persistence ---

    public void saveEvents() {
        try (Writer writer = new FileWriter(EVENTS_FILE)) {
            gson.toJson(events, writer);
        } catch (IOException e) {
            logger.error("Failed to save Secret Santa events to file: {}", EVENTS_FILE, e);
        }
    }

    private void loadEvents() {
        File file = new File(EVENTS_FILE);
        if (!file.exists())
            return;

        try (Reader reader = new FileReader(EVENTS_FILE)) {
            Type type = new TypeToken<ArrayList<SecretSantaEvent>>() {
            }.getType();

            List<SecretSantaEvent> loaded = gson.fromJson(reader, type);

            if (loaded != null)
                this.events = loaded;
        } catch (IOException e) {
            logger.error("Failed to load Secret Santa events from file: {}", EVENTS_FILE, e);
        }
    }

    public User getRecipientFor(SecretSantaEvent event, User santa) {
        if (event == null || !event.isDrawDone())
            return null;

        return event.getDrawResults().stream()
                .filter(pair -> pair.getSanta().getLogin().equals(santa.getLogin()))
                .map(SecretSantaUsersPair::getRecipient)
                .findFirst()
                .orElse(null);
    }
}

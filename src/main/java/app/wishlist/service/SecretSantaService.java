package app.wishlist.service;

import app.wishlist.model.SecretSantaUsersPair;
import app.wishlist.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecretSantaService {

    private static final SecretSantaService INSTANCE = new SecretSantaService();

    private LocalDate eventDate;
    private List<SecretSantaUsersPair> drawResults = new ArrayList<>();
    private boolean isDrawDone = false;

    private SecretSantaService() {
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

    // THE ALGORITHM
    public void performDraw(List<User> participants) {
        if (participants == null || participants.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 participants!");
        }

        // 1. Create a shuffled copy of the list
        List<User> givers = new ArrayList<>(participants);
        List<User> receivers = new ArrayList<>(participants);

        // Simple Rotation Algorithm:
        // Shuffle the list. Person at index i gives to person at index i+1.
        // Last person gives to first person.
        // This guarantees one loop and no self-assignments.
        Collections.shuffle(givers);

        drawResults.clear();

        for (int i = 0; i < givers.size(); i++) {
            User santa = givers.get(i);
            // The receiver is the next person in the list (wrapping around)
            User recipient = givers.get((i + 1) % givers.size());

            drawResults.add(new SecretSantaUsersPair(santa, recipient));
        }

        isDrawDone = true;
        System.out.println("Draw completed! generated " + drawResults.size() + " pairs.");
    }

    // Helper to find who I am buying for
    public User getRecipientFor(User santa) {
        if (!isDrawDone) return null;

        return drawResults.stream()
                .filter(pair -> pair.getSanta().getLogin().equals(santa.getLogin()))
                .map(SecretSantaUsersPair::getRecipient)
                .findFirst()
                .orElse(null);
    }

    // Admin helper: See all pairs (for debugging/admin table)
    public List<SecretSantaUsersPair> getAllPairs() {
        return drawResults;
    }
}

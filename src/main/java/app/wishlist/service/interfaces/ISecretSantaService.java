package app.wishlist.service.interfaces;

import app.wishlist.model.domain.event.SecretSantaEvent;
import app.wishlist.model.domain.user.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for Secret Santa event management operations.
 */
public interface ISecretSantaService {

    // Event Management
    SecretSantaEvent createEvent(String name, User owner, LocalDate date);

    List<SecretSantaEvent> getMyEvents(User user);

    // Participant Management
    void addParticipant(SecretSantaEvent event, User user);

    void removeParticipant(SecretSantaEvent event, User user);

    // Draw Operations
    void performDraw(SecretSantaEvent event);

    User getRecipientFor(SecretSantaEvent event, User santa);

    // Persistence
    void saveEvents();
}

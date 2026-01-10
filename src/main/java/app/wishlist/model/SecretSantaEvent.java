package app.wishlist.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SecretSantaEvent {
    private String id;
    private String name;
    private String ownerLogin; // The "Admin" of this specific event
    private String eventDate;  // Stored as String for JSON simplicity
    private boolean isDrawDone;

    private List<String> participantLogins = new ArrayList<>();

    private List<SecretSantaUsersPair> drawResults = new ArrayList<>();

    public SecretSantaEvent(String name, String ownerLogin, LocalDate date) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.ownerLogin = ownerLogin;
        this.eventDate = date.toString();
        this.isDrawDone = false;
    }

    public LocalDate getLocalDate() {
        return eventDate == null ? null : LocalDate.parse(eventDate);
    }
}

package app.wishlist.model.domain.event;

import app.wishlist.model.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecretSantaUsersPair {
    private User santa; // Mikolaj
    private User recipient; // Obdarowany
}

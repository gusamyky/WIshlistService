package app.wishlist.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecretSantaUsersPair {
    private User santa;       // Mikolaj
    private User recipient;   // Obdarowany
}

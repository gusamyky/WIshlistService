package app.wishlist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishItem {
    private String id; // Unique ID for finding items
    private String name;
    private String description;
    private double price;
    private String imageUrl;

    // Reservation Logic
    private boolean isReserved;
    private String reservedByUserLogin; // The login of the Santa who bought it
}

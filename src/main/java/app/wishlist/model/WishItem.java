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
    private String id;
    private String name;
    private String description;
    private MonetaryAmount price;
    private String imageUrl;
    private boolean isReserved;
    private String reservedByUserLogin;
}

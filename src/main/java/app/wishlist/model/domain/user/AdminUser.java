package app.wishlist.model.domain.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminUser extends User {

    public AdminUser(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
        this.type = "ADMIN";
    }
}

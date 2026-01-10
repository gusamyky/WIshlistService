package app.wishlist.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminUser extends User {

    private boolean superAccess = true;

    public AdminUser(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
        this.type = "ADMIN";
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}

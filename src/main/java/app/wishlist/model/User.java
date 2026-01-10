package app.wishlist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    protected String login;
    protected String password; // Salt:Hash
    protected String firstName;
    protected String lastName;
    protected String type = "USER"; // Discriminator for Polymorphism

    protected Set<String> friends = new HashSet<>();

    public User(String login, String password, String firstName, String lastName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = "USER";
    }

    public Set<String> getFriends() {
        if (friends == null) friends = new HashSet<>();
        return friends;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Polymorphic method example
    public boolean isAdmin() {
        return this.type.equals("ADMIN");
    }
}

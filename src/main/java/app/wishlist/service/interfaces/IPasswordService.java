package app.wishlist.service.interfaces;

/**
 * Interface for password hashing operations.
 */
public interface IPasswordService {

    /**
     * Generate a random salt for password hashing.
     *
     * @return byte array containing random salt
     */
    byte[] getSalt();

    /**
     * Hash a password using the provided salt.
     *
     * @param password the password to hash
     * @param salt     the salt to use
     * @return Base64-encoded hash string
     */
    String hashPassword(String password, byte[] salt);
}

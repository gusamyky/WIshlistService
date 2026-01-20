package app.wishlist.service.impl;

import app.wishlist.service.interfaces.IPasswordService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Implementation of IPasswordService providing password hashing functionality
 * using PBKDF2 algorithm.
 */
public class PasswordHashingServiceImpl implements IPasswordService {
    // PBKDF2 Constants
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private static final PasswordHashingServiceImpl INSTANCE = new PasswordHashingServiceImpl();

    private PasswordHashingServiceImpl() {
    }

    public static PasswordHashingServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public String hashPassword(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }
}

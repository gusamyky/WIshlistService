package app.wishlist.service.persistence;

/**
 * Interface for generic persistence operations.
 *
 * @param <T> the type of data to persist
 */
public interface IPersistenceService<T> {

    /**
     * Save data to persistent storage.
     *
     * @param data     the data to save
     * @param filePath the file path for storage
     */
    void save(T data, String filePath);

    /**
     * Load data from persistent storage.
     *
     * @param filePath the file path to load from
     * @param type     the type token for deserialization
     * @return the loaded data, or null if file doesn't exist or error occurs
     */
    T load(String filePath, Class<T> type);
}

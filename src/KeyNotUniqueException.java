public class KeyNotUniqueException extends Exception {
    // class release version:
    private static final long serialVersionUID = 1L;

    public KeyNotUniqueException(String key) {
        super("Key is not unique: " + key);
    }
}

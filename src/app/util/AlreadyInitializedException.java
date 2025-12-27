package app.util;

public class AlreadyInitializedException extends IllegalStateException {
    public AlreadyInitializedException() {
        super();
    }

    public AlreadyInitializedException(String message) {
        super(message);
    }

    public AlreadyInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyInitializedException(Throwable cause) {
        super(cause);
    }
}

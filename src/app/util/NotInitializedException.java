package app.util;

public class NotInitializedException extends IllegalStateException {
    public NotInitializedException() {
        super();
    }

    public NotInitializedException(String s) {
        super(s);
    }

    public NotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInitializedException(Throwable cause) {
        super(cause);
    }
}

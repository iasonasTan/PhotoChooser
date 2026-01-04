package app.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Properties;

public class OutputProperties {
    private final Properties mProperties = new Properties();

    public OutputProperties() {
    }

    public OutputProperties put(String key, String value) {
        mProperties.setProperty(Objects.requireNonNull(key), Objects.requireNonNull(value));
        return this;
    }

    public OutputProperties put(String key, boolean value) {
        mProperties.setProperty(key, String.valueOf(value));
        return this;
    }

    public OutputProperties put(String key, int value) {
        mProperties.setProperty(key, String.valueOf(value));
        return this;
    }

    public OutputProperties put(String key, long value) {
        mProperties.setProperty(key, String.valueOf(value));
        return this;
    }

    public OutputProperties put(String key, float value) {
        mProperties.setProperty(key, String.valueOf(value));
        return this;
    }

    public OutputProperties put(String key, double value) {
        mProperties.setProperty(key, String.valueOf(value));
        return this;
    }

    public void store(OutputStream outputStream) {
        try {
            mProperties.store(outputStream, "Updated Settings");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

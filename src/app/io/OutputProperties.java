package app.io;

import app.util.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

@SuppressWarnings("unused")
public class OutputProperties {
    private final Properties mProperties = new Properties();

    public OutputProperties() {
    }

    public void put(String key, String value) {
        mProperties.put(key, value);
    }

    public void put(String key, boolean value) {
        mProperties.put(key, String.valueOf(value));
    }

    public void put(String key, int value) {
        mProperties.put(key, String.valueOf(value));
    }

    public void put(String key, float value) {
        mProperties.put(key, String.valueOf(value));
    }

    public void store(OutputStream outputStream) {
        try {
            mProperties.store(outputStream, "Updated Settings");
        } catch (IOException e) {
            Utils.showException(e);
            throw new RuntimeException(e);
        }
    }
}

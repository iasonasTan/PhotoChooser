package app.io;

import app.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("unused")
public class InputProperties {
    private final Properties mProperties = new Properties();

    public InputProperties(InputStream inputStream) {
        try {
            mProperties.load(inputStream);
        } catch (IOException e) {
            Utils.showException(e);
            throw new RuntimeException(e);
        }
    }

    public String getString(String key) {
        return mProperties.getProperty(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        if(value == null)
            return defaultValue;
        else
            return Boolean.parseBoolean(getString(key));
    }

    public int getInteger(String key, int defaultValue) throws NumberFormatException {
        String value = getString(key);
        if(value == null)
            return defaultValue;
        else
            return Integer.parseInt(getString(key));
    }

    public float getFloat(String key, float defaultValue) throws NumberFormatException {
        String value = getString(key);
        if(value == null)
            return defaultValue;
        else
            return Float.parseFloat(getString(key));
    }
}

package app.io;

import app.util.AlreadyInitializedException;
import app.util.NotInitializedException;
import app.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Supplies methods that stores/reads files from user's config directory.
 * This class automatically detects the OS the app runs on.
 * @see Configuration#createConfigFile(String)
 */
public final class Configuration {
    /**
     * After {@link #init(String)} call this field holds the Application's config directory that's inside user's config directory.
     * Before {@link #init(String)} call this field holds value {@code null} to flag that the utility is not initialized.
     * @see Configuration#check()
     */
    private static String sConfigDirectory = null;

    /**
     * Initializes utility class.
     * Finds user's config directory based on current OS.
     * Creates application's configuration folder.
     */
    public static void init(String appName) {
        if(sConfigDirectory!=null)
            throw new AlreadyInitializedException();
        Path systemConfigDir;
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        if(osName.contains("win")) {
            systemConfigDir = Paths.get(System.getenv("APPDATA"));
        } else if (osName.contains("mac")) {
            systemConfigDir = Paths.get(userHome, "Library", "Application Support");
        } else { // Unix, Linux
            systemConfigDir = Paths.get(userHome, ".config");
        }
        Path configDir = Paths.get(systemConfigDir.toAbsolutePath().toString(), appName);
        if(!Files.exists(configDir) || !Files.isDirectory(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                Utils.showException(e);
                throw new RuntimeException(e);
            }
        }
        sConfigDirectory = configDir.toAbsolutePath().toString();
    }

    /**
     * Creates {@link OutputStream} from the given path.
     * If file doesn't exist, method creates it.
     * @param path Config file's path as {@link String}.
     * @return {@link OutputStream} from the given path.
     * @see #createConfigFile
     */
    public static OutputStream getConfigOutputStream(String path) {
        check();
        try {
            return Files.newOutputStream(createConfigFile(path));
        } catch (IOException e) {
            Utils.showException(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates {@link InputStream} from the given path.
     * If file doesn't exist, method creates it.
     * @param path Config file's path as {@link String}.
     * @param createFile If {@code true}, method will create file if it doesn't exist.
     * @return {@link InputStream} from the given path.
     * @see #createConfigFile(String)
     */
    public static InputStream getConfigInputStream(String path, boolean createFile) {
        check();
        Path out = Paths.get(sConfigDirectory, path);
        if(createFile)
            createConfigFile(path);
        try {
            return Files.newInputStream(out);
        } catch (IOException e) {
            Utils.showException(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates file with given path in user config directory and returns it's {@link Path}.
     * @param filePath path to file as {@link String}.
     * @return {@link Path} to file inside application's config directory.
     */
    public static Path createConfigFile(String filePath) {
        check();
        Path out = Paths.get(sConfigDirectory, filePath);
        try {
            Files.createDirectories(out.getParent());
            if (!Files.exists(out)) {
                Files.createFile(out);
            }
        } catch (IOException e) {
            Utils.showException(e);
            throw new RuntimeException(e);
        }
        return out;
    }

    public static void loadProperties(String filePath, InputProperties properties) {
        try(InputStream inputStream = getConfigInputStream(filePath, true)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void storeProperties(String filePath, OutputProperties properties) {
        try (OutputStream stream = getConfigOutputStream(filePath)) {
            properties.store(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Checks if utility is initialized.
     * @see #sConfigDirectory
     * @see #init(String)
     */
    private static void check() {
        if(sConfigDirectory == null)
            throw new NotInitializedException();
    }

    private Configuration(){}
}

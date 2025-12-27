package app.util;

import app.io.Configuration;
import app.io.InputProperties;
import app.ui.VerticalFlowLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class Utils {
    private static BufferedImage sCannotLoadImage;
    private static boolean sEnableSFX;

    public static void init() {
        try (InputStream resourceStream = Utils.class.getResourceAsStream("cannotloadimage.png");
            InputStream configurationStream = Configuration.getConfigInputStream("settings.properties", true)) {
            if(resourceStream==null) throw new NullPointerException("Goto catch");
            InputProperties inputProperties = new InputProperties(configurationStream);
            sEnableSFX = inputProperties.getBoolean("enable_sfx", true);
            sCannotLoadImage = ImageIO.read(resourceStream);
        } catch (IOException | NullPointerException e) {
            showException(e);
        }
    }

    public static void showException(Exception exception) {
        String messageText = String.format("""
                        The application has a bug (%s).
                        The reason: %s
                        Please report this to the developer.
                        """,
                exception.getClass().getName(),
                exception.getMessage()==null?"No details":exception.getMessage()
        );
        Image image = loadImage("alert.png");
        image = image.getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH);
        Object message = createPanel(new VerticalFlowLayout(10, 10), toJLabels((Object[]) messageText.split("\n")));
        JOptionPane.showMessageDialog(null,  message,"Unexpected Error", JOptionPane.ERROR_MESSAGE, new ImageIcon(image));
    }

    public static JLabel[] toJLabels(Object... objects) {
        JLabel[] labels = new JLabel[objects.length];
        for (int i = 0; i < objects.length; i++) {
            labels[i] = new JLabel(objects[i].toString());
        }
        return labels;
    }

    public static boolean endsWith(String string, String... ends) {
        boolean out = false;
        for (String end: ends) {
            out = out||string.endsWith(end);
        }
        return out;
    }

    public static JPanel createPanel(LayoutManager layout, Component... components) {
        if(layout==null || components==null)
            throw new NullPointerException();
        JPanel panel = new JPanel(layout);
        for (Component component : components) {
            panel.add(component);
        }
        return panel;
    }

    @Deprecated
    public static JFrame startOnFrame(JPanel gui, String title) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(gui);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public static long getRemainingFiles(String path) {
        try (Stream<Path> files = Files.list(Paths.get(path))) {
            return files.filter(Files::isRegularFile).count();
        } catch (IOException _) {
            return 0;
        }
    }

    public static BufferedImage loadImage(String path) {
        if(sCannotLoadImage==null)
            throw new NotInitializedException();
        try (InputStream inputStream=Utils.class.getResourceAsStream(path)) {
            if(inputStream==null)
                throw new NullPointerException("Goto catch");
            return ImageIO.read(inputStream);
        } catch (IOException | NullPointerException _) {
            System.err.println("Couldn't load given image. Returning warning image.");
            return sCannotLoadImage;
        }
    }

    private Utils(){}
}

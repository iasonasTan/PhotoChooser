package app.main;

import app.io.*;
import app.ui.AbstractScreen;
import app.ui.VerticalFlowLayout;
import app.util.AlreadyInitializedException;
import app.util.ImagePathList;
import app.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gui extends AbstractScreen {
    public static Gui instance = null;

    public static void fromDirectory(String path) {
        if(instance != null)
            throw new AlreadyInitializedException();
        instance = new Gui(path);
    }

    public static void fromChooser() {
        FileLoader.instance.getDirectory(f -> fromDirectory(f.getAbsolutePath()));
    }

    private final ImageHandler mImageHandler = new ImageHandler();
    // gui
    private final JLabel mLabel = new JLabel(),
            mCountLabel=new JLabel();
    private final JButton mKeepButton = new JButton("Keep"),
            mDeleteButton = new JButton("Delete"),
            //mSettingsButton = new JButton("Settings"),
            mExitButton = new JButton("Exit");
    private final JCheckBox mChoiceCheckBox = new JCheckBox("Show Remaining Files");
    private ImageHandler.ImageData mImageData;
    private int mCount = 0;

    private Gui(String pathStr) {
        initSwing();
        IO.println("Working with images at "+pathStr);
        Path p = Paths.get(pathStr);
        var images = new ImagePathList();
        FileLoader.instance.loadFiles(p, images);
        mImageHandler.setFiles(images)
                .setRootPath(p)
                .setImagesWidth(AbstractScreen.sSize.width-200);
        nextImage();
    }

    @Override
    protected String title() {
        return "Photo Chooser - Main";
    }

    @Override
    protected Image icon() {
        return Utils.loadImage("/res/app_icon.png");
    }

    private void styleComponents(JComponent... jcs) {
        try {
            // noinspection all
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/ibmplexsans_bold.ttf"));
            for (JComponent jc : jcs) {
                jc.setFont(font.deriveFont(17f));
                jc.setPreferredSize(new Dimension(180, 30));
                jc.setFocusable(false);
            }
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initSwing() {
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        JComponent[] comps = {mKeepButton, mDeleteButton, mExitButton, mCountLabel, mChoiceCheckBox};
        mCountLabel.setForeground(Color.WHITE);
        styleComponents(comps);
        JPanel panel = Utils.createPanel(new VerticalFlowLayout(10, 10), comps);
        panel.setBackground(Color.DARK_GRAY);
        add(panel);
        add(mLabel);

        mDeleteButton.addActionListener(createDeleteListener());
        mDeleteButton.setFocusable(false);
        mKeepButton.addActionListener(createKeepListener());
        mKeepButton.setFocusable(false);
        mExitButton.addActionListener(createExitListener(getFrame()));

        mChoiceCheckBox.addChangeListener(_ -> Configuration.storeProperties(
                "settings.properties",
                new OutputProperties().put("showRemaining", mChoiceCheckBox.isSelected())
        ));

        InputProperties properties = new InputProperties();
        Configuration.loadProperties("settings.properties", properties);
        mChoiceCheckBox.setSelected(properties.getBoolean("showRemaining", true));
        mChoiceCheckBox.setBackground(Color.DARK_GRAY);
        mChoiceCheckBox.setForeground(Color.WHITE);

        setFocusable(true);
        addKeyListener(new ActionKeyListener());
        requestFocus();

        setVisible();
    }

    private ActionListener createExitListener(final Window window) {
        return _ -> {
            InputProperties properties = new InputProperties();
            Configuration.loadProperties("settings.properties", properties);
            if(properties.getBoolean("showRemaining", true)) {
                JOptionPane.showMessageDialog(this, """
                        Images you deleted are not deleted. Just moved into '__trash__' folder.
                        You have to delete this folder yourself.
                        This happens for security reasons.""");
                JOptionPane.showMessageDialog(this, "Remaining Files: "+mImageHandler.getRemaining());
            }

            IO.println("Moving files...");
            try {
                mImageHandler.close();
            } catch (IOException e) {
                IO.println("[ERROR] Cannot close ImageHandler. "+e);
            }
            window.dispose();
        };
    }

    private void nextImage() {
        mImageData = mImageHandler.nextImageData();
        if(mImageData==null) {
            JOptionPane.showMessageDialog(this, "No more images.");
            mExitButton.doClick();
        } else {
            mCount++;
            mCountLabel.setText("Shown Images: "+mCount);
            System.out.println(String.valueOf(mImageData.image()==null).toUpperCase()+mImageData.path());
            mLabel.setIcon(mImageData.image());
        }
    }

    private ActionListener createKeepListener() {
        return _ -> {
            IO.println("[DEBUG] Keeping image");
            mImageHandler.keep(mImageData.path());
            nextImage();
        };
    }

    private ActionListener createDeleteListener() {
        return _ -> {
            IO.println("[DEBUG] Deleting image");
            mImageHandler.delete(mImageData.path());
            nextImage();
        };
    }

    private final class ActionKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    mDeleteButton.doClick();
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    mKeepButton.doClick();
                    break;
                case KeyEvent.VK_ESCAPE:
                    mExitButton.doClick();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
        }
    }
}

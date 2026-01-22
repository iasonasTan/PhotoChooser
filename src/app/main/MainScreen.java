package app.main;

import app.io.FileLoader;
import app.io.ImageHandler;
import app.util.ImagePathList;
import lib.UtilAlreadyInitializedException;
import lib.UtilNotInitializedException;
import lib.gui.AbstractScreen;
import lib.gui.layout.VerticalFlowLayout;
import lib.gui.style.SimpleStyleLoader;
import lib.gui.style.SimpleStyler;
import lib.gui.style.Style;
import lib.gui.style.Styler;
import lib.io.Configuration;
import lib.io.InputProperties;
import lib.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainScreen extends AbstractScreen {
    private static MainScreen sInstance = null;

    public static MainScreen getInstance() {
        if(sInstance==null)
            throw new UtilNotInitializedException();
        return sInstance;
    }

    public static void fromDirectory(String path) {
        if(sInstance != null)
            throw new UtilAlreadyInitializedException();
        DisplayMode displayMode = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDisplayMode();
        Dimension screenSize = new Dimension(displayMode.getWidth(), displayMode.getHeight());
        sInstance = new MainScreen(path, screenSize);
        sInstance.setVisible();
    }

    public static void fromChooser() {
        FileLoader.instance.getDirectory(f -> fromDirectory(f.getAbsolutePath()));
    }

    // logic
    private final ImageHandler mImageHandler;
    private int mCount = 0;

    // gui
    private final JLabel mLabel = new JLabel(),
            mCountLabel=new JLabel();
    private final JButton mKeepButton = new JButton("Keep"),
            mDeleteButton = new JButton("Delete"),
            mSettingsButton = new JButton("Settings"),
            mSkipButton = new JButton("Skip"),
            mExitButton = new JButton("Exit");

    private MainScreen(String pathStr, Dimension screenSize) {
        initSwing();

        Path rootPath = Paths.get(pathStr);
        var imagesList = new ImagePathList();
        FileLoader.instance.loadFiles(rootPath, imagesList);

        int controlComponentsWidth = 200;
        int imageSize = Math.min(screenSize.width-controlComponentsWidth, screenSize.height);
        mImageHandler = ImageHandler.newInstance(rootPath, imagesList, imageSize);

        nextImage();
    }

    @Override
    protected String title() {
        return "Photo Chooser - Main";
    }

    @Override
    protected Image background() {
        return null;
    }

    @Override
    protected Image icon() {
        return Resources.loadImage("/appres/icons/app_icon.png");
    }

    private void initSwing() {
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        JComponent[] comps = {
                mKeepButton, mDeleteButton, mExitButton, mSkipButton,
                mSettingsButton, mCountLabel
        };

        Style style = SimpleStyleLoader.instance.loadStyle("/appres/styles/gui.style");
        Styler styler = new SimpleStyler(style);
        styler.styleComponents(comps);

        addComponentBuilder(new JPanel(), null)
                .setSize(new Dimension(190, 500))
                .setLayout(new VerticalFlowLayout(10, 10))
                .setBackground(Color.DARK_GRAY)
                .addChildren(comps)
                .build();

        add(mLabel);

        mSkipButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_SKIP));
        mDeleteButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_DELETE));
        mKeepButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_KEEP));
        mExitButton.addActionListener(new ExitListener());
        mSettingsButton.addActionListener(new ShowSettingsListener());

        setFocusable(true);
        addKeyListener(new ActionKeyListener());
        requestFocus();
    }

    private void nextImage() {
        ImageHandler.ImageData mImageData = mImageHandler.nextImageData();
        if(mImageData==null) {
            JOptionPane.showMessageDialog(this, "No more images.");
            mExitButton.doClick();
        } else {
            mCount++;
            mCountLabel.setText("Shown Images: "+mCount);
            System.out.println(String.valueOf(mImageData.image()==null).toUpperCase()+ mImageData.path());
            mLabel.setIcon(mImageData.image());
        }
    }

    private final class ActionKeyListener implements KeyListener {
        @Override public void keyPressed(KeyEvent keyEvent) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_D:
                    mDeleteButton.doClick();
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_K:
                    mKeepButton.doClick();
                    break;
                case KeyEvent.VK_END:
                case KeyEvent.VK_SHIFT:
                case KeyEvent.VK_S:
                    mSkipButton.doClick();
                    break;
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_E:
                    mExitButton.doClick();
                    break;
            }
        }

        @Override public void keyReleased(KeyEvent keyEvent) {}
        @Override public void keyTyped(KeyEvent keyEvent) {}
    }

    private static class ShowSettingsListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            SettingsScreen.getInstance().setVisible();
        }
    }

    private class ExitListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            InputProperties properties = Configuration.loadProperties("settings.properties");
            if(properties.getBoolean("showRemaining", true)) {
                JOptionPane.showMessageDialog(MainScreen.this, Resources.loadText("/appres/texts/exit_message.txt"));
                JOptionPane.showMessageDialog(MainScreen.this, "Remaining Files: "+mImageHandler.getRemaining());
            }
            IO.println("Moving files...");
            try {
                mImageHandler.close();
            } catch (IOException e) {
                IO.println("[ERROR] Cannot close ImageHandler. "+e);
            }
            AbstractScreen.dispose();
        }
    }

    private class FileHandlerListener implements ActionListener {
        public static final String ACTION_KEEP   = "Keep";
        public static final String ACTION_DELETE = "Delete";
        public static final String ACTION_SKIP   = "Skip";

        private final String mAction;

        public FileHandlerListener(String action) {
            mAction = action;
        }

        @Override public void actionPerformed(ActionEvent actionEvent) {
            IO.println("[DEBUG] "+mAction+" image");
            switch(mAction) {
                case ACTION_KEEP -> mImageHandler.keep();
                case ACTION_DELETE -> mImageHandler.delete();
            }
            nextImage();
        }
    }
}

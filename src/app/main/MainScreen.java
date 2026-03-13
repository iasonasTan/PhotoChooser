package app.main;

import app.io.FileLoader;
import app.io.ImageHandler;
import app.ui.AppAbstractScreen;
import app.ui.Colors;
import app.ui.StyleManager;
import app.util.ImagePathList;
import lib.AlreadyInitializedException;
import lib.NotInitializedException;
import lib.gui.AbstractScreen;
import lib.gui.layout.VerticalFlowLayout;
import lib.gui.style.*;
import lib.io.Configuration;
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

public class MainScreen extends AppAbstractScreen {
    private static MainScreen sInstance = null;

    public static MainScreen getInstance() {
        if(sInstance==null)
            throw new NotInitializedException();
        return sInstance;
    }

    public static void fromDirectory(String path) {
        if(sInstance != null)
            throw new AlreadyInitializedException();
        DisplayMode displayMode = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDisplayMode();
        Dimension screenSize = new Dimension(displayMode.getWidth(), displayMode.getHeight());
        sInstance = new MainScreen(path, screenSize);
        sInstance.visible(true);
    }

    public static void fromChooser() {
        FileLoader.instance.getDirectory(f -> fromDirectory(f.getAbsolutePath()));
    }

    // logic
    private final ImageHandler mImageHandler;
    private int mCount = 0, mGoal;
    private ImageHandler.ImageData mImageData;

    // gui
    private JLabel mImageLabel;
    private JLabel mCountLabel;
    private JButton mKeepButton, mDeleteButton, mSkipButton, mExitButton, mUndoButton;

    private MainScreen(String pathStr, Dimension screenSize) {
        Path rootPath = Paths.get(pathStr);
        var imagesList = new ImagePathList();
        FileLoader.instance.loadFiles(rootPath, imagesList);

        int controlComponentsWidth = 200;
        int imageSize = Math.min(screenSize.width-controlComponentsWidth, screenSize.height);
        mImageHandler = ImageHandler.newInstance(rootPath, imagesList, imageSize);
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

    @Override
    protected void initSwing() {
        Styler styler = new SimpleStyler(StyleManager.getStyle(StyleManager.STYLE_GUI));
        ComponentFactory factory = new ComponentFactory(styler);
        mCountLabel = factory.newComponent(JLabel.class);
        mKeepButton = factory.newComponent(JButton.class, "Keep");
        mDeleteButton = factory.newComponent(JButton.class, "Delete");
        mSkipButton = factory.newComponent(JButton.class, "Skip");
        mExitButton = factory.newComponent(JButton.class, "Exit");
        mUndoButton = factory.newComponent(JButton.class, "Undo");
        mImageLabel = new JLabel();
        JButton settingsButton = factory.newComponent(JButton.class, "Settings");
        JLabel goalLabel = factory.newComponent(JLabel.class, "No Goal");

        setLayout(new FlowLayout());
        setBackground(Colors.getColor().background());

        JComponent[] comps = {
                mKeepButton, mDeleteButton, mExitButton, mSkipButton, mUndoButton,
                settingsButton, mCountLabel, goalLabel
        };

        addComponentBuilder(new JPanel(), null)
                .setSize(new Dimension(190, 500))
                .setLayout(new VerticalFlowLayout(10, 10))
                .setBackground(Colors.getColor().background())
                .addChildren(comps)
                .build();

        add(mImageLabel);

        mDeleteButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_DELETE));
        mSkipButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_SKIP));
        mKeepButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_KEEP));
        mUndoButton.addActionListener(new FileHandlerListener(FileHandlerListener.ACTION_UNDO));
        settingsButton.addActionListener(new ShowSettingsListener());
        mExitButton.addActionListener(new ExitListener());

        setFocusable(true);
        addKeyListener(new ActionKeyListener());
        requestFocus();

        if(!isCreated()) {
            nextImage();
        } else {
            mImageLabel.setIcon(mImageData.image());
        }
        mCountLabel.setText("Shown Images: "+mCount);

        mGoal = Configuration.loadProperties("settings.properties")
                        .getInteger("goal", -1);
        if(mGoal != -1)
            goalLabel.setText("Goal: "+mGoal);
    }

    @SuppressWarnings("all") // Intellij false-possitive
    private void nextImage() {
        mImageData = mImageHandler.nextImageData();
        if(mImageData==null) {
            JOptionPane.showMessageDialog(this, "No more images.");
            mExitButton.doClick();
        } else {
            mCount++;
            mCountLabel.setText("Shown Images: "+mCount);
            System.out.printf("%s exists: %B\n", mImageData.path(), mImageData.image()==null);
            mImageLabel.setIcon(mImageData.image());
            if(mGoal > 0 && mCount > mGoal) {
                JOptionPane.showMessageDialog(this, "Goal of "+mGoal+" pictures is complete!");
                mGoal = -1;
            }
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
                case KeyEvent.VK_U:
                case KeyEvent.VK_Z:
                    mUndoButton.doClick();
                    break;
            }
        }

        @Override public void keyReleased(KeyEvent keyEvent) {}
        @Override public void keyTyped(KeyEvent keyEvent) {}
    }

    private static class ShowSettingsListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            SettingsScreen.getInstance().visible(true);
        }
    }

    private class ExitListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            IO.println("Moving files...");
            try {
                mImageHandler.close();
            } catch (IOException e) {
                IO.println("[ERROR] Failed to close ImageHandler "+e);
            }
            IO.println("All files are moved.");
            AbstractScreen.dispose();
            if(Configuration.loadProperties("settings.properties").getBoolean("showRemaining", true)) {
                JOptionPane.showMessageDialog(MainScreen.this, Resources.loadText("/appres/texts/exit_message.txt"));
                JOptionPane.showMessageDialog(MainScreen.this, "Remaining Files: "+mImageHandler.getRemaining());
            }
            System.exit(0);
        }
    }

    private class FileHandlerListener implements ActionListener {
        public static final String ACTION_KEEP   = "Keep";
        public static final String ACTION_DELETE = "Delete";
        public static final String ACTION_SKIP   = "Skip";
        public static final String ACTION_UNDO   = "Undo";

        private final String mAction;

        public FileHandlerListener(String action) {
            mAction = action;
        }

        @Override public void actionPerformed(ActionEvent actionEvent) {
            IO.println("[DEBUG] "+mAction+" image");
            switch(mAction) {
                case ACTION_KEEP -> mImageHandler.move(ImageHandler.Destination.KEEP);
                case ACTION_DELETE -> mImageHandler.move(ImageHandler.Destination.TRASH);
                case ACTION_UNDO -> mImageHandler.undo();
                case ACTION_SKIP -> {}
            }
            if(!ACTION_UNDO.equals(mAction))
                nextImage();
        }
    }
}

package app.main;

import lib.UtilAlreadyInitializedException;
import lib.gui.AbstractScreen;
import lib.gui.UI;
import lib.gui.layout.VerticalFlowLayout;
import lib.gui.style.*;
import lib.io.Configuration;
import lib.io.InputProperties;
import lib.io.OutputProperties;
import lib.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsScreen extends AbstractScreen {
    private static SettingsScreen sInstance;

    public static SettingsScreen getInstance() {
        return sInstance;
    }

    public static void init() {
        if(sInstance !=null)
            throw new UtilAlreadyInitializedException();
        sInstance = new SettingsScreen();
    }

    private final JButton mExitButton;
    private final JCheckBox mNativeFullscreenCheckBox;
    private final JTextArea mInstructionsTextArea;
    private final JCheckBox mShowRemainingCheckBox;

    /* Initialize GUI components */
    {
        Style style = SimpleStyleLoader.instance.loadStyle("/appres/styles/settings.style");
        Styler styler = new SimpleStyler(style);
        ComponentFactory factory = new ComponentFactory(styler);
        mExitButton = factory.newComponent(JButton.class, "Save & Exit");
        mNativeFullscreenCheckBox = factory.newComponent(JCheckBox.class, "Native Fullscreen");
        mInstructionsTextArea = factory.newComponent(JTextArea.class, Resources.loadText("/appres/texts/instructions.txt"));
        mShowRemainingCheckBox = factory.newComponent(JCheckBox.class, "Show Remaining Files");
    }

    private SettingsScreen() {
        initSwing();
        loadSettings();
    }

    @SuppressWarnings("all") // intellij false-possitive
    private void loadSettings() {
        InputProperties properties = Configuration.loadProperties("settings.properties");
        mNativeFullscreenCheckBox.setSelected(properties.getBoolean("native_fullscreen", true));
        mShowRemainingCheckBox.setSelected(properties.getBoolean("show_remaining", true));
    }

    private void initSwing() {
        setLayout(new GridBagLayout());
        setBackground(Color.GRAY);

        // noinspection all : intellij false-possitive
        mInstructionsTextArea.setPreferredSize(new Dimension(1000, 1000));
        mInstructionsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(mInstructionsTextArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        addComponentBuilder(new JPanel(), new GridBagConstraints())
                .setSize(new Dimension(510, 700))
                .setLayout(new VerticalFlowLayout(5, 5))
                .addChildren(mNativeFullscreenCheckBox, mShowRemainingCheckBox, mExitButton, scrollPane)
                .setBackground(Color.DARK_GRAY)
                .build();

        // noinspection all : intellij false-possitive
        mExitButton.addActionListener(new ExitListener());
    }

    @Override
    protected String title() {
        return "Photo Chooser - Settings";
    }

    @Override
    protected Image background() {
        return null;
    }

    @Override
    protected Image icon() {
        return Resources.loadImage("/appres/icons/app_icon_settings.png");
    }

    private final class ExitListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            OutputProperties properties = new OutputProperties();
            properties.put("native_fullscreen", mNativeFullscreenCheckBox.isSelected());
            properties.put("show_remaining", mShowRemainingCheckBox.isSelected());
            Configuration.storeProperties("settings.properties", properties);
            MainScreen.getInstance().setVisible();
        }
    }
}

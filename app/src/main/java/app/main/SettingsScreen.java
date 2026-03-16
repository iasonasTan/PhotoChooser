package app.main;

import app.ui.AppAbstractScreen;
import app.ui.Colors;
import app.ui.StyleManager;
import lib.AlreadyInitializedException;
import lib.gui.layout.VerticalFlowLayout;
import lib.gui.style.*;
import lib.io.Configuration;
import lib.io.InputProperties;
import lib.io.OutputProperties;
import lib.io.Resources;

import javax.swing.*;
import java.awt.*;

public class SettingsScreen extends AppAbstractScreen {
    private static SettingsScreen sInstance;

    /*
    Bad-Logic.
    These static variables are here because of library limitations.
    Best solution:
    Load properties from class GoalScreen and just put these variables in there.
    Problem:
    Library doesn't allow it.
    Library update is necessary to remove this logic.
    It is also possible to store this in a different configuration file.
     */
    static int sGoal;
    static boolean sAutoClose;

    public static SettingsScreen getInstance() {
        return sInstance;
    }

    public static void init() {
        if(sInstance !=null)
            throw new AlreadyInitializedException();
        sInstance = new SettingsScreen();
    }

    private JCheckBox mNativeFullscreenCheckBox, mShowRemainingCheckBox, mNightThemeCheckBox, mRandomOrderCheckbox;

    private void loadSettings() {
        InputProperties properties = Configuration.loadProperties("settings.properties");
        mNativeFullscreenCheckBox.setSelected(properties.getBoolean("native_fullscreen", true));
        mShowRemainingCheckBox.setSelected(properties.getBoolean("show_remaining", true));
        mRandomOrderCheckbox.setSelected(properties.getBoolean("random_order", true));
        mNightThemeCheckBox.setSelected(properties.getBoolean("night_theme", false));
        sGoal = properties.getInteger("goal", 5);
    }

    @Override
    protected void initSwing() {
        Styler styler = new SimpleStyler(StyleManager.getStyle(StyleManager.STYLE_SETTINGS));
        ComponentFactory factory = new ComponentFactory(styler);
        mNativeFullscreenCheckBox = factory.newComponent(JCheckBox.class, "Native Fullscreen");
        mShowRemainingCheckBox = factory.newComponent(JCheckBox.class, "Show Remaining Files");
        mNightThemeCheckBox = factory.newComponent(JCheckBox.class, "Night theme");
        mRandomOrderCheckbox = factory.newComponent(JCheckBox.class, "Show pictures with random order");
        JButton exitButton = factory.newComponent(JButton.class, "Save & Exit");
        JButton setGoalButton = factory.newComponent(JButton.class, "Set Goal");
        JTextArea instructionsTextArea = factory.newComponent(
                JTextArea.class,
                Resources.loadText("/appres/texts/instructions.txt")
        );

        setLayout(new GridBagLayout());
        setBackground(Colors.getColor().background());

        // noinspection all : intellij false-possitive
        instructionsTextArea.setPreferredSize(new Dimension(1000, 1000));
        instructionsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(instructionsTextArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JComponent[] children = {
                mNativeFullscreenCheckBox,
                mShowRemainingCheckBox,
                mRandomOrderCheckbox,
                mNightThemeCheckBox,
                setGoalButton,
                exitButton,
                scrollPane
        };

        addComponentBuilder(new JPanel(), new GridBagConstraints())
                .setSize(new Dimension(510, 700))
                .setLayout(new VerticalFlowLayout(5, 5))
                .addChildren(children)
                .setBackground(Colors.getColor().background())
                .build();

        exitButton.addActionListener(_ -> {
            IO.println("Storing goal: "+sGoal);
            OutputProperties properties = new OutputProperties()
                    .put("native_fullscreen", mNativeFullscreenCheckBox.isSelected())
                    .put("show_remaining", mShowRemainingCheckBox.isSelected())
                    .put("random_order", mRandomOrderCheckbox.isSelected())
                    .put("night_theme", mNightThemeCheckBox.isSelected())
                    .put("auto_close_app", sAutoClose)
                    .put("goal", sGoal);
            Configuration.storeProperties("settings.properties", properties);
            Colors.load(mNightThemeCheckBox.isSelected());
            MainScreen.getInstance().visible(true);
        });

        setGoalButton.addActionListener(_ -> GoalScreen.getInstance().visible(true));

        loadSettings();
    }

    @Override
    protected String title() {
        return "Photo Chooser - Settings";
    }

    @Override
    protected Image icon() {
        return Resources.loadImage("/appres/icons/app_icon_settings.png");
    }

    @Override
    protected Image background() {
        return null; // No background image
    }
}

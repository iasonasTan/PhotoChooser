package app.main;

import app.ui.AppAbstractScreen;
import app.ui.Colors;
import app.ui.StyleManager;
import lib.AlreadyInitializedException;
import lib.gui.layout.VerticalFlowLayout;
import lib.gui.style.ComponentFactory;
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

public class GoalScreen extends AppAbstractScreen {
    private static GoalScreen sInstance;

    public static GoalScreen getInstance() {
        return sInstance;
    }

    public static void init() {
        if(sInstance !=null)
            throw new AlreadyInitializedException();
        sInstance = new GoalScreen();
    }

    private JTextField mCountField;

    @Override
    protected void initSwing() {
        Style style = StyleManager.getStyle(StyleManager.STYLE_SETTINGS);
        Styler styler = new SimpleStyler(style);
        ComponentFactory factory = new ComponentFactory(styler);
        JButton setAndExitButton = factory.newComponent(JButton.class, "Done");
        JButton resetButton = factory.newComponent(JButton.class, "Reset and Exit");
        mCountField= factory.newComponent(JTextField.class);

        setLayout(new GridBagLayout());
        setBackground(Colors.getColor().background());

        JComponent[] children = {
                mCountField,
                setAndExitButton,
                resetButton
        };

        addComponentBuilder(new JPanel(), new GridBagConstraints())
                .setSize(new Dimension(510, 700))
                .setLayout(new VerticalFlowLayout(5, 5))
                .addChildren(children)
                .setBackground(Colors.getColor().background())
                .build();

        setAndExitButton.addActionListener(new ButtonListener(ButtonListener.ACTION_EXIT));
        resetButton.addActionListener(new ButtonListener(ButtonListener.ACTION_RESET));

        loadSettings();
    }

    private void loadSettings() {
        InputProperties properties = Configuration.loadProperties("settings.properties");
        mCountField.setText(String.valueOf(properties.getInteger("goal", 5)));
    }

    private int getTypedGoal() {
        try {
            int goalRaw = Integer.parseInt(mCountField.getText());
            if(goalRaw <= 0)
                return -1;
            return goalRaw;
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "This is not a number. Goal will not be set!");
            return -1;
        }
    }

    private final class ButtonListener implements ActionListener {
        public static final String ACTION_EXIT = "app.ui.goal.listener.exit";
        public static final String ACTION_RESET = "app.ui.goal.listener.reset" + ACTION_EXIT;

        private final String mAction;

        private ButtonListener(String action) {
            if(action==null)
                throw new NullPointerException("Action cannot be null.");
            this.mAction = action;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(mAction.contains(ACTION_EXIT)) {
                SettingsScreen.getInstance().visible(true);
            }
            SettingsScreen.sGoal = mAction.contains(ACTION_RESET) ? -1 : getTypedGoal();
            IO.println("Goal: "+SettingsScreen.sGoal);
        }
    }

    @Override
    protected String title() {
        return "Goal Editor - Photo Chooser";
    }

    @Override
    protected Image background() {
        return null; // No background image
    }

    @Override
    protected Image icon() {
        return Resources.loadImage("/appres/icons/app_icon_settings.png");
    }
}

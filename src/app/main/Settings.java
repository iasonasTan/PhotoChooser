package app.main;

import app.io.Configuration;
import app.io.InputProperties;
import app.io.OutputProperties;
import app.ui.AbstractScreen;
import app.ui.VerticalFlowLayout;
import app.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("all")
@Deprecated
public class Settings extends AbstractScreen {
    @Deprecated
    public static final Settings instance = new Settings();
    private final JButton mExitButton = new JButton("Save & Exit");

    private Settings() {
        initSwing();
        loadSettings();
    }

    private void loadSettings() {
        InputProperties properties = new InputProperties();
        Configuration.loadProperties("settings.properties", properties);
    }

    private void initSwing() {
        JPanel panel = Utils.createPanel(new VerticalFlowLayout(), mExitButton);
        add(panel, new GridBagConstraints());
        mExitButton.addActionListener(new ExitListener());
    }

    public class ExitListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            OutputProperties properties = new OutputProperties();
            Configuration.storeProperties("settings.properties", properties);
            Gui.instance.setVisible();
        }
    }

    @Override
    protected String title() {
        return "Photo Chooser - Settings";
    }

    @Override
    protected Image icon() {
        return Utils.loadImage("/res/app_icon_settings.png");
    }
}

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
import java.io.InputStream;
import java.io.OutputStream;

public class Settings extends AbstractScreen {
    public static final Settings instance = new Settings();
    private final JCheckBox mSFXCheckBox = new JCheckBox("Sound Effects");
    private final JButton mExitButton = new JButton("Save & Exit");

    private Settings() {
        initSwing();
        loadSettings();
    }

    private void loadSettings() {
        try {
            InputStream inputStream = Configuration.getConfigInputStream("settings.properties", true);
            InputProperties properties = new InputProperties(inputStream);
            inputStream.close();
            mSFXCheckBox.setSelected(properties.getBoolean("enable_sfx", true));
        } catch (IOException e) {
            Utils.showException(e);
        }
    }

    private void initSwing() {
        add(Utils.createPanel(new VerticalFlowLayout(), mSFXCheckBox, mExitButton), new GridBagConstraints());
        mExitButton.addActionListener(new ExitListener());
    }

    public class ExitListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent actionEvent) {
            try {
                OutputProperties properties = new OutputProperties();
                properties.put("enable_sfx", mSFXCheckBox.isSelected());
                OutputStream outputStream = Configuration.getConfigOutputStream("settings.properties");
                properties.store(outputStream);
                outputStream.close();
            } catch (IOException e) {
                Utils.showException(e);
            }
            Gui.instance.setVisible();
        }
    }

    @Override
    protected String title() {
        return "Photo Chooser - Settings";
    }

    @Override
    protected Image icon() {
        return Utils.loadImage("/app_icon_settings.png");
    }
}

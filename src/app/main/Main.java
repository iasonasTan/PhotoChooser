package app.main;

import app.ui.Colors;
import lib.gui.UI;
import lib.io.Configuration;
import lib.io.Resources;

public class Main {
    public static void main(String[] args) {
        Configuration.init("photo_chooser");
        UI.init();
        Resources.init(Main.class);
        boolean nightTheme = Configuration
                .loadProperties("settings.properties")
                .getBoolean("night_theme", false);
        Colors.load(nightTheme);
        SettingsScreen.init();
        GoalScreen.init();

        if(args.length == 0)
            MainScreen.fromChooser();
        else
            MainScreen.fromDirectory(args[0]);
    }
}
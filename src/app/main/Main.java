package app.main;

import lib.gui.UI;
import lib.io.Configuration;
import lib.io.Resources;

public class Main {
    public static void main(String[] args) {
        Configuration.init("photo_chooser");
        UI.init();
        Resources.init();
        SettingsScreen.init();

        if(args.length == 0)
            MainScreen.fromChooser();
        else
            MainScreen.fromDirectory(args[0]);
    }
}

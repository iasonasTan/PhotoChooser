package app.main;

import lib.gui.UI;
import lib.io.Configuration;
import lib.io.Resources;

/*
TODO Fix 'show remaining files'
TODO Update Lib.jar on this project
TODO Upload on site
TODO Use XML Swing builder from Lib.jar
 */

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

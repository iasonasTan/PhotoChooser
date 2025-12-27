package app.main;

import app.io.Configuration;
import app.ui.AbstractScreen;
import app.util.Utils;

public class Main {
    public static void main(String[] args) {
        Configuration.init("photo_chooser");
        AbstractScreen.init();
        Utils.init();

        if(args.length == 0)
            Gui.fromChooser();
        else
            Gui.fromDirectory(args[0]);
    }
}

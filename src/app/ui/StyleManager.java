package app.ui;

import lib.gui.style.SimpleStyleLoader;
import lib.gui.style.Style;
import lib.io.Configuration;

public final class StyleManager {
    public static final String STYLE_SETTINGS = "settings";
    public static final String STYLE_GUI      = "gui";

    public static Style getStyle(String pageType) {
        boolean nightTheme = Configuration
                .loadProperties("settings.properties")
                .getBoolean("night_theme", false);
        String styleFileName = nightTheme ? pageType+"-night.style" : pageType+".style";
        return SimpleStyleLoader.instance.loadStyle("/appres/styles/"+styleFileName);
    }

    private StyleManager(){
    }
}

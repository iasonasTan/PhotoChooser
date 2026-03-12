package app.ui;

import lib.NotInitializedException;

import java.awt.*;

public final class Colors {
    private static ColorSet sColorSet;

    private static final ColorSet NIGHT_COLORS = new RecordColorSet(
            Color.DARK_GRAY,
            Color.WHITE
    );

    private static final ColorSet DAY_COLORS = new RecordColorSet(
            Color.LIGHT_GRAY,
            Color.BLACK
    );

    public static void load(boolean night) {
        sColorSet = night ? NIGHT_COLORS : DAY_COLORS;
    }

    public static ColorSet getColor(){
        if(sColorSet == null)
            throw new NotInitializedException();
        return sColorSet;
    }

    public interface ColorSet {
        Color background();
        Color foreground();
    }

    private record RecordColorSet(Color background, Color foreground) implements ColorSet {

    }

    private Colors() {
    }
}

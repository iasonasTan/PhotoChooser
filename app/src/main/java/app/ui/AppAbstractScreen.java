package app.ui;

import lib.gui.AbstractScreen;

public abstract class AppAbstractScreen extends AbstractScreen {
    private boolean mCreated = false;

    public AppAbstractScreen() {
        super();
    }

    public boolean isCreated() {
        return mCreated;
    }

    @Override
    public AbstractScreen visible(boolean fullscreen) {
        removeAll();
        initSwing();
        mCreated = true;
        return super.visible(fullscreen);
    }

    protected abstract void initSwing();
}

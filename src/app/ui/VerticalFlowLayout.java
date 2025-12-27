package app.ui;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("unused")
public final class VerticalFlowLayout implements LayoutManager2 {
    private final ArrayList<Component> mComponents = new ArrayList<>();
    public int mVerticalGap = 0, mHorizontalGap = 0;

    public VerticalFlowLayout() {
    }

    public VerticalFlowLayout(int horizontalGap, int verticalGap) {
        this.mHorizontalGap = horizontalGap;
        this.mVerticalGap = verticalGap;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        mComponents.add(comp);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {

    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        mComponents.add(comp);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        mComponents.remove(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension out = new Dimension(mHorizontalGap, mVerticalGap);
        for(Component comp: mComponents) {
            Dimension compSize = comp.getPreferredSize();
            out.width = Math.max(out.width, compSize.width)+mHorizontalGap*2;
            out.height += compSize.height+mVerticalGap;
        }
        return out;
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        int x = mHorizontalGap;
        int y = mVerticalGap;

        int maxWidth = 0;

        Dimension parentSize = parent.getPreferredSize();

        for (Component c: mComponents) {
            if (!c.isVisible()) {
                continue;
            }

            Dimension componentSize = c.getPreferredSize();
            if (maxWidth < componentSize.width) {
                maxWidth = componentSize.width;
            }

            c.setBounds(x, y, componentSize.width, componentSize.height);

            y+=componentSize.height+ mVerticalGap;

            if (y+componentSize.height > parentSize.height) {
                y = mVerticalGap;
                x+= mHorizontalGap +maxWidth;
                maxWidth = mVerticalGap;
            }

        }

    }
}

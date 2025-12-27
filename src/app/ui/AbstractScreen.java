package app.ui;

import app.util.AlreadyInitializedException;
import app.util.NotInitializedException;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractScreen extends JPanel implements Screen {
    private static JFrame sFrame;
    public static final Dimension sSize = new Dimension(1200, 900);

    public static void init(){
        if(sFrame!=null)
            throw new AlreadyInitializedException();
        sFrame = new JFrame();
        sFrame.setSize(sSize);
        //sFrame.setResizable(false); // TODO
        sFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        sFrame.setLocationRelativeTo(null);
    }

    public AbstractScreen() {
        if(sFrame==null)
            throw new NotInitializedException();

        setPreferredSize(sFrame.getSize());
    }

    protected abstract String title();
    protected abstract Image icon();

    protected JFrame getFrame() {
        return sFrame;
    }

    @Override
    public void setVisible() {
        setPreferredSize(sSize);
        setSize(sSize);

        sFrame.setContentPane(this);
        sFrame.setIconImage(icon());
        sFrame.setTitle(title());

        if(!sFrame.isVisible())
            sFrame.setVisible(true);

        sFrame.revalidate();
        sFrame.repaint();
    }
}

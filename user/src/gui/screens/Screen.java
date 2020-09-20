package gui.screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;

public abstract class Screen implements ActionListener {
    private final JFrame frame = new JFrame("Java socket chatroom");
    protected final EmptyBorder defaultPadding = new EmptyBorder(100, 100, 100, 100);

    public Screen() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public final JFrame getFrame() {
        return this.frame;
    }
}
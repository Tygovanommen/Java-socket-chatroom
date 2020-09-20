package gui.screens;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Loading extends Screen {

    public Loading() {
        JLabel loadingLabel = new JLabel("Connecting...");

        JPanel panel = new JPanel();
        panel.add(loadingLabel);

        // Add padding
        panel.setBorder(this.defaultPadding);

        this.getFrame().add(panel);

        this.getFrame().pack();
        this.getFrame().setLocationRelativeTo(null);
        this.getFrame().setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
package gui.screens;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Login extends Screen {

    private String userName;
    private final JTextField usernameChooser;

    public Login() {
        usernameChooser = new JTextField(15);
        JButton enterServer = new JButton("OK");
        enterServer.addActionListener(this);
        this.getFrame().getRootPane().setDefaultButton(enterServer);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameChooser);
        panel.add(enterServer);

        // Add padding
        panel.setBorder(this.defaultPadding);

        this.getFrame().add(panel);

        this.getFrame().pack();
        this.getFrame().setLocationRelativeTo(null);
        this.getFrame().setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameChooser.getText();
        if (username.length() < 1) {
            JOptionPane.showMessageDialog(this.getFrame(), "Username can't be empty. Please enter again.");
        } else {
            this.userName = username;
        }
    }

    public String getUserName() {
        return this.userName;
    }
}

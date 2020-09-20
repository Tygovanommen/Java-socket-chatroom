package gui;

import org.json.simple.JSONObject;
import user.User;
import user.UserSocket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

public class ChatRoom implements ActionListener, Runnable {

    private final String appName = "Java socket chatroom";

    private final JFrame chatFrame = new JFrame(appName);
    private final LinkedList<String> newMessages = new LinkedList<>();
    private boolean messageWaiting = false;
    private UserSocket userSocket;
    private JButton sendMessage;
    private JTextField messageBox;
    private JTextArea chatBox;

    private JFrame loginFrame;
    private JTextField usernameChooser;
    private User user;

    private JFrame loadingFrame;

    private String activeListener;

    public ChatRoom() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.loginScreen();
    }

    public void loginScreen() {
        chatFrame.setVisible(false);
        loginFrame = new JFrame(appName);

        JLabel chooseUsernameLabel = new JLabel("Username:");
        usernameChooser = new JTextField(15);
        JButton enterServer = new JButton("OK");
        this.activeListener = "Login";
        enterServer.addActionListener(this);
        loginFrame.getRootPane().setDefaultButton(enterServer);

        JPanel panel = new JPanel();
        panel.add(chooseUsernameLabel);
        panel.add(usernameChooser);
        panel.add(enterServer);

        // Add padding
        panel.setBorder(new EmptyBorder(100, 100, 100, 100));

        loginFrame.add(panel);

        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    public void loadingScreen() {
        loginFrame.setVisible(false);

        loadingFrame = new JFrame(appName);

        JLabel chooseUsernameLabel = new JLabel("Connecting...");

        JPanel panel = new JPanel();
        panel.add(chooseUsernameLabel);

        // Add padding
        panel.setBorder(new EmptyBorder(100, 100, 100, 100));

        loadingFrame.add(panel);

        loadingFrame.pack();
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setVisible(true);
    }

    public void chatScreen(UserSocket userSocket) {
        this.userSocket = userSocket;

        this.loadingFrame.setVisible(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

        sendMessage = new JButton("Send Message");
        this.activeListener = "Chat";
        sendMessage.addActionListener(this);
        chatFrame.getRootPane().setDefaultButton(sendMessage);

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        // Add padding
        mainPanel.setBorder(new EmptyBorder(100, 100, 100, 100));

        chatBox.append("Connected!\n");

        mainPanel.setPreferredSize(new Dimension(640, 480));

        chatFrame.add(mainPanel);
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.pack();
        chatFrame.setLocationRelativeTo(null);
        chatFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.activeListener.equals("Chat")) {
            this.userMessanger();
        } else if (this.activeListener.equals("Login")) {
            String username = usernameChooser.getText();
            if (username.length() < 1) {
                JOptionPane.showMessageDialog(this.loginFrame, "Username can't be empty. Please enter again.");
            } else {
                this.user = new User(username);
                loadingScreen();
            }
        }
    }

    private void userMessanger() {
        if (messageBox.getText().length() >= 1) {
            this.addNextMessage(messageBox.getText());
            messageBox.setText("");
            messageBox.requestFocusInWindow();
        }
    }

    public void addNextMessage(String message) {
        if (userSocket.getAccessThread().isAlive()) {
            synchronized (this.newMessages) {
                this.messageWaiting = true;
                this.newMessages.push(message);
            }
        }
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public void run() {
        try {
            Socket socket = this.userSocket.getSocket();
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), false);
            InputStream serverInStream = socket.getInputStream();
            Scanner serverIn = new Scanner(serverInStream);

            // While socket connection is still active
            while (!socket.isClosed()) {
                // If there is a new message from other users
                if (serverInStream.available() > 0) {
                    if (serverIn.hasNextLine()) {
                        chatBox.append(serverIn.nextLine() + "\n");
                        chatBox.setCaretPosition(chatBox.getText().length());
                    }
                }

                // If there are new messages from current user
                if (this.messageWaiting) {
                    String nextSend = "";
                    synchronized (this.newMessages) {
                        nextSend = this.newMessages.pop();
                        this.messageWaiting = !this.newMessages.isEmpty();
                    }

                    // Output message
                    JSONObject json = new JSONObject();
                    json.put("name", user.getName());
                    json.put("message", nextSend);
                    json.put("timezone", TimeZone.getDefault().getID());

                    serverOut.println(json.toString());
                    serverOut.flush();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
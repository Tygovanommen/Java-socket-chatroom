package gui.screens;

import org.json.simple.JSONObject;
import user.User;
import user.UserSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

public class Chat extends Screen implements Runnable {

    private final LinkedList<String> newMessages = new LinkedList<>();
    private boolean messageWaiting = false;
    private UserSocket userSocket;
    private JButton sendMessage;
    private JTextField messageBox;
    private JTextArea chatBox;
    private final User user;

    public Chat(User user, UserSocket userSocket) {
        this.user = user;
        this.userSocket = userSocket;
    }

    public void open() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(this);
        this.getFrame().getRootPane().setDefaultButton(sendMessage);

        JLabel roomLabel = new JLabel("Room: Home");
        mainPanel.add(roomLabel, BorderLayout.NORTH);

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
        mainPanel.setBorder(defaultPadding);

        chatBox.append("Connected!\n");

        mainPanel.setPreferredSize(new Dimension(640, 480));

        this.getFrame().add(mainPanel);
        this.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getFrame().pack();
        this.getFrame().setLocationRelativeTo(null);
        this.getFrame().setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (messageBox.getText().length() >= 1) {
            if (userSocket.getAccessThread().isAlive()) {
                synchronized (this.newMessages) {
                    this.messageWaiting = true;
                    this.newMessages.push(messageBox.getText());
                }
            }
            messageBox.setText("");
            messageBox.requestFocusInWindow();
        }
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
                    json.put("name", this.user.getName());
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
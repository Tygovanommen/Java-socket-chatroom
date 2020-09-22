package user;

import gui.screens.Chat;
import gui.screens.Loading;
import gui.screens.Login;

public class Main {

    // Desired socket port and host
    private static final int port = 5000;
    private static final String host = "localhost";

    public static void main(String[] args) {
        UserSocket userSocket = new UserSocket(host, port);

        Login loginScreen = new Login();

        // Wait till username is set
        while (loginScreen.getUserName() == null) {
            System.out.println(loginScreen.getUserName());
            if (loginScreen.getUserName() != null) {
                // Connect to server
                loginScreen.getFrame().dispose();
                Chat chatScreen = new Chat(userSocket);
                Loading loadScreen = new Loading();
                boolean connected = userSocket.connectSocket(chatScreen, loginScreen.getUserName());
                if (connected) {
                    // Open chat screen
                    loadScreen.getFrame().dispose();
                    chatScreen.open();
                } else {
                    System.exit(1);
                }
            }
        }
    }
}
package user;

import gui.ChatRoom;

public class Main {

    // Desired socket port and host
    private static final int port = 5000;
    private static final String host = "localhost";

    public static void main(String[] args) {
        UserSocket userSocket = new UserSocket(host, port);

        ChatRoom chatRoom = new ChatRoom();

        // Wait till username is set
        while (chatRoom.getUser() == null) {
            System.out.println(chatRoom.getUser());
            if (chatRoom.getUser() != null) {
                // Connect to server
                boolean connected = userSocket.connectSocket(chatRoom);
                if (connected) {
                     // Open chat screen
                    chatRoom.chatScreen(userSocket);
                } else {
                    System.exit(1);
                }
            }
        }
    }
}
package user;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class UserThread {

    private final String host;
    private final int port;

    /**
     * @param host socket host to connect to
     * @param port socket port to connect to
     */
    public UserThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Establish connection to socket and wait till user sends message
     * @param user current user
     */
    public void startUserThread(User user) {
        try {
            // Establish connection to socket server
            System.out.println("Connecting...");
            Socket socket = new Socket(this.host, this.port);
            Thread.sleep(1000);
            System.out.println("Connected!");

            // Start new user thread
            ServerThread serverThread = new ServerThread(socket, user.getName());
            Thread serverAccessThread = new Thread(serverThread);
            serverAccessThread.start();

            serverThread.addNextMessage(user.getName() + " joined server.");

            // Wait for new user input
            Scanner scan = new Scanner(System.in);
            while (serverAccessThread.isAlive()) {
                if (scan.hasNextLine()) {
                    serverThread.addNextMessage(user.getName() + ": " + scan.nextLine());
                }
            }
        } catch (IOException | InterruptedException ex) {
            System.err.println("Something went wrong while connecting.");
            ex.printStackTrace();
        }
    }
}
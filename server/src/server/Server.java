package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private final int port;
    private final List<User> allThreads = new ArrayList<>();

    /**
     * @param port port the socket should connect to.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Start socket server
     */
    public void startServer() {
        try {
            // Create socket connection
            ServerSocket socketServer = new ServerSocket(this.port);
            acceptUsers(socketServer);
        } catch (IOException e) {
            System.err.println("Could not connect to port: " + this.port);
            System.exit(1);
        }
    }

    /**
     * Grant access to new users
     *
     * @param serverSocket current server socket connection
     */
    private void acceptUsers(ServerSocket serverSocket) {
        // Show current socket address information
        System.out.println("Server port: " + serverSocket.getLocalSocketAddress());
        while (true) {
            try {
                // Accept new user
                Socket socket = serverSocket.accept();
                String username = (new Scanner( socket.getInputStream() )).nextLine();
                username = username.replace(",", "").replace(" ", "_");
                System.out.println("New Client: \"" + username + "\"\n\t     Host:" + socket.getRemoteSocketAddress());

                // create new User
                User newUser = new User(socket, username);

                // Add user to User list
                this.allThreads.add(newUser);

                // Create a new thread incoming message of new user
                new Thread(new Receiver(this, newUser)).start();

                // Send welcome message
                newUser.getOutStream().println("Welcome to the server!");

            } catch (IOException ex) {
                System.out.println("Failed accepting new user on port: " + this.port);
            }
        }
    }

    public List<User> getThreadsByRoom(String room) {
        List<User> threads = new ArrayList<>();
        for (User user : this.allThreads) {
            if (user.getRoom().equals(room)) {
                threads.add(user);
            }
        }
        return threads;
    }
}
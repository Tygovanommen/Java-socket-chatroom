package user;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class UserSocket {

    private final String host;
    private final int port;
    private Socket socket;
    private Thread accessThread;
    private String userName;

    /**
     * @param host socket host to connect to
     * @param port socket port to connect to
     */
    public UserSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Establish connection to socket
     */
    public boolean connectSocket(String userName, Runnable runnable) {
        try {
            this.userName = userName;
            // Establish connection to socket server
            System.out.println("Connecting...");
            this.socket = new Socket(this.host, this.port);
            Thread.sleep(1000);
            System.out.println("Connected!");

            // Get Socket output stream
            PrintStream output = new PrintStream(this.socket.getOutputStream());

            // send nickname to server
            output.println(userName);

            this.accessThread = new Thread(runnable);
            this.accessThread.start();
            return true;
        } catch (IOException | InterruptedException ex) {
            System.out.println("Something went wrong while connecting to server");
            return false;
        }
    }

    public String getUserName() {
        return this.userName;
    }

    public Thread getAccessThread() {
        return this.accessThread;
    }

    public Socket getSocket() {
        return this.socket;
    }
}
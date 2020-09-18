package user;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerThread implements Runnable {

    private final Socket socket;
    private final String userName;
    private final LinkedList<String> newMessages;
    private boolean messageWaiting = false;

    /**
     * @param socket current socket connection
     * @param userName current username
     */
    public ServerThread(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
        this.newMessages = new LinkedList<>();
    }

    /**
     * Send message to server
     * @param message message that should be send
     */
    public void addNextMessage(String message) {
        synchronized (this.newMessages) {
            this.messageWaiting = true;
            this.newMessages.push(message);
        }
    }

    /**
     * Print new message on screen and get messages from other users
     */
    @Override
    public void run() {
        try {
            PrintWriter serverOut = new PrintWriter(this.socket.getOutputStream(), false);
            InputStream serverInStream = this.socket.getInputStream();
            Scanner serverIn = new Scanner(serverInStream);

            // While socket connection is still active
            while (!this.socket.isClosed()) {
                // If there is a new message from current user
                if (serverInStream.available() > 0) {
                    // Output message
                    if (serverIn.hasNextLine()) {
                        System.out.println(serverIn.nextLine());
                    }
                }

                // If there are new messages from other users
                if (this.messageWaiting) {
                    String nextSend = "";
                    synchronized (this.newMessages) {
                        nextSend = this.newMessages.pop();
                        this.messageWaiting = !this.newMessages.isEmpty();
                    }
                    // Output message
                    serverOut.println(nextSend);
                    serverOut.flush();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
package user;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

public class ServerThread implements Runnable {

    private final Socket socket;
    private String room;
    private final User user;
    private final LinkedList<String> newMessages;
    private boolean messageWaiting = false;

    /**
     * @param socket current socket connection
     */
    public ServerThread(Socket socket, User user, String room) {
        this.socket = socket;
        this.room = room;
        this.user = user;
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
                // If there is a new message from other users
                if (serverInStream.available() > 0) {
                    if (serverIn.hasNextLine()) {
                        System.out.println(serverIn.nextLine());
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
                    json.put("room", this.room);
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
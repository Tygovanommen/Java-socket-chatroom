package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Messanger implements Runnable {

    private final Socket socket;
    private PrintWriter userOut;
    private final Server server;
    private String room;

    /**
     * @param server current server object
     * @param socket current socket connection
     */
    public Messanger(Server server, Socket socket, String room) {
        this.server = server;
        this.socket = socket;
        this.room = room;
    }

    /**
     * Wait for user to send message and show it to other users
     */
    @Override
    public void run() {
        try {
            this.userOut = new PrintWriter(this.socket.getOutputStream(), false);
            Scanner in = new Scanner(this.socket.getInputStream());

            // While socket connection is still active
            while (!this.socket.isClosed()) {
                if (in.hasNextLine()) {
                    // Decode JSON created by user
                    JSONObject json = (JSONObject) new JSONParser().parse(in.nextLine());

                    // Send message
                    Command command = new Command((String) json.get("message"));
                    if (command.roomChange()) {
                        this.room = "room1";

                        for (Messanger thread : this.server.getThreadsByRoom(this.room)) {
                            sendMessage(json.get("name") + " joined room", thread);
                        }

                    }
                    if (command.isCommand()) {
                        // Server message
                        sendMessage(command.getMessage(), this);
                    } else {
                        // Normal message
                        String message = "(" + new SimpleDateFormat("h:mm a").format(new Date()) + ") ";
                        message += "(ROOM: " + this.room + ") ";
                        message += json.get("name") + ": " + json.get("message");

                        if (this.room.equals("start")) {
                            sendMessage(message, this);
                        } else {
                            for (Messanger thread : this.server.getThreadsByRoom(this.room)) {
                                sendMessage(message, thread);
                            }
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message message to send
     * @param thread to what user/thread the message should be send
     */
    private void sendMessage(String message, Messanger thread) {
        PrintWriter userOut = thread.getWriter();
        if (userOut != null) {
            userOut.println(message);
            userOut.flush();
        }
    }

    private PrintWriter getWriter() {
        return this.userOut;
    }

    public String getRoom() {
        return this.room;
    }
}
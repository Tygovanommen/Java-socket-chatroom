package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Receiver implements Runnable {

    private final Socket socket;
    private PrintWriter userOut;
    private final Server server;
    private String room = "start";

    /**
     * @param server current server object
     * @param socket current socket connection
     */
    public Receiver(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
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

                    String message = (String) json.get("message");

                    // Send message
                    Command command = new Command(message);
                    Map<String, Receiver> sendThreads = new HashMap<>();
                    if (command.isCommand()) {
                        if (command.roomChange()) {
                            // Get second word of command to change room name
                            try {
                                this.room = message.split(" ")[1];

                                // Show join message
                                for (Receiver thread : this.server.getThreadsByRoom(this.room)) {
                                    sendThreads.put(json.get("name") + " joined room", thread);
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                sendThreads.put("Please fill in a room name.", this);
                            }
                        } else {
                            // Server message
                            sendThreads.put(command.getMessage(), this);
                        }
                    } else {
                        // Normal message
                        if (this.room.equals("start")) {
                            sendThreads.put("You're currently in no room, change room by typing /room {room_name}", this);
                        } else {
                            String next = "(" + new SimpleDateFormat("h:mm a").format(new Date()) + ") ";
                            next += json.get("name") + ": " + message;
                            for (Receiver thread : this.server.getThreadsByRoom(this.room)) {
                                sendThreads.put(next, thread);
                            }
                        }
                    }
                    // Send the message
                    for (Map.Entry<String, Receiver> entry : sendThreads.entrySet()) {
                        sendMessage(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message message to send
     * @param thread  to what user/thread the message should be send
     */
    private void sendMessage(String message, Receiver thread) {
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
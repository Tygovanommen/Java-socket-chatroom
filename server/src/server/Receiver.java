package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Receiver implements Runnable {

    private final User user;
    private final Server server;
    private String room = "start";

    /**
     * @param server current server object
     * @param user current user connection
     */
    public Receiver(Server server, User user) {
        this.server = server;
        this.user = user;
    }

    /**
     * Wait for user to send message and show it to other users
     */
    @Override
    public void run() {
        try {
            Scanner in = new Scanner(this.user.getInputStream());

            // While socket connection is still active
            while (!this.user.isSocketClosed()) {
                if (in.hasNextLine()) {
                    // Decode JSON created by user
                    JSONObject json = (JSONObject) new JSONParser().parse(in.nextLine());

                    String message = (String) json.get("message");

                    // Send message
                    Command command = new Command(message);
                    Map<String, User> sendThreads = new HashMap<>();
                    if (command.isCommand()) {
                        if (command.roomChange()) {
                            // Get second word of command to change room name
                            try {
                                this.room = message.split(" ")[1];
                                this.user.setRoom(room);

                                // Show join message
                                for (User thread : this.server.getThreadsByRoom(this.room)) {
                                    sendThreads.put(thread.getUsername() + " joined room", thread);
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                sendThreads.put("Please fill in a room name.", this.user);
                            }
                        } else {
                            // Server message
                            sendThreads.put(command.getMessage(), this.user);
                        }
                    } else {
                        // Normal message
                        if (this.room.equals("start")) {
                            sendThreads.put("You're currently in no room, change room by typing /room {room_name}", this.user);
                        } else {
                            String next = "(" + new SimpleDateFormat("h:mm a").format(new Date()) + ") ";
                            next += this.user.getUsername() + ": " + message;
                            for (User thread : this.server.getThreadsByRoom(this.room)) {
                                sendThreads.put(next, thread);
                            }
                        }
                    }
                    // Send the message
                    for (Map.Entry<String, User> entry : sendThreads.entrySet()) {
                        sendMessage(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message message to send
     * @param thread  to what user/thread the message should be send
     */
    private void sendMessage(String message, User thread) {
        PrintStream userOut = thread.getOutStream();
        if (userOut != null) {
            userOut.println(message);
            userOut.flush();
        }
    }
}
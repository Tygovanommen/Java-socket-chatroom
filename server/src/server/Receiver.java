package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Receiver implements Runnable {

    private final User user;
    private final Server server;
    private String room = "start";

    /**
     * @param server current server object
     * @param user   current user connection
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
                    Command command = new Command(message, this.server);
                    JSONObject jsonReturn;
                    if (command.isCommand()) {
                        if (command.roomChange()) {
                            // Get second word of command to change room name
                            try {
                                this.room = message.split(" ")[1];
                                this.user.setRoom(room);

                                // Show join message
                                for (User thread : this.server.getThreadsByRoom(this.room)) {
                                    jsonReturn = new JSONObject();
                                    jsonReturn.put("message", this.user.getUsername() + " joined room");
                                    sendMessage(jsonReturn.toString(), thread);
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                jsonReturn = new JSONObject();
                                jsonReturn.put("message", "You are currently in room: " + this.room);
                                sendMessage(jsonReturn.toString(), this.user);
                            }
                        } else {
                            // Server message
                            jsonReturn = new JSONObject();
                            jsonReturn.put("message", command.getMessage());
                            sendMessage(jsonReturn.toString(), this.user);
                        }
                    } else {
                        // Normal message
                        if (this.room.equals("start")) {
                            jsonReturn = new JSONObject();
                            jsonReturn.put("message", "You're currently in no room, change room by typing /room {room_name})");
                            sendMessage(jsonReturn.toString(), this.user);
                        } else {
                            String date = new SimpleDateFormat("h:mm a").format(new Date());
                            for (User thread : this.server.getThreadsByRoom(this.room)) {
                                jsonReturn = new JSONObject();
                                jsonReturn.put("username", this.user.getUsername());
                                jsonReturn.put("user_color", this.user.getColor());
                                jsonReturn.put("message", message);
                                jsonReturn.put("time", date);
                                sendMessage(jsonReturn.toString(), thread);
                            }
                        }
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
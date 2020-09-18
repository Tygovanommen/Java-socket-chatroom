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

public class ThreadControl implements Runnable {

    private final Socket socket;
    private PrintWriter userOut;
    private final Server server;
    private String room;

    /**
     * @param server current server object
     * @param socket current socket connection
     */
    public ThreadControl(Server server, Socket socket) {
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
                    String input = in.nextLine();

                    // Decode JSON
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(input);

                    // If help called, only send message to yourself
                    if (json.get("message").equals("/help")) {
                        PrintWriter userOut = this.getWriter();
                        if (userOut != null) {
                            userOut.println("HELP CALLED");
                            userOut.flush();
                        }
                        break;
                    }

                    // Set current room
                    this.room = (String) json.get("room");

                    for (ThreadControl user : this.server.getThreadsByRoom(this.room)) {
                        PrintWriter userOut = user.getWriter();
                        if (userOut != null) {
                            String nextSend = "(" + new SimpleDateFormat("h:mm a").format(new Date()) + ") ";
                            nextSend += json.get("name") + ": " + json.get("message");
                            userOut.println(nextSend);
                            userOut.flush();
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private PrintWriter getWriter() {
        return this.userOut;
    }

    public String getRoom() {
        return this.room;
    }
}
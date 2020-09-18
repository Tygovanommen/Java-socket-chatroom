package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UserThread implements Runnable {

    private final Socket socket;
    private PrintWriter userOut;
    private final Server server;

    /**
     * @param server current server object
     * @param socket current socket connection
     */
    public UserThread(Server server, Socket socket) {
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
                    for (UserThread user : this.server.getAllThreads()) {
                        PrintWriter userOut = user.getWriter();
                        if (userOut != null) {
                            String nextSend = "(" + new SimpleDateFormat("h:mm a").format(new Date()) + ") ";
                            nextSend += input;
                            userOut.println(nextSend);
                            userOut.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PrintWriter getWriter() {
        return this.userOut;
    }
}
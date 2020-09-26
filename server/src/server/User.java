package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class User {
    private static int nbUser = 0;
    private final PrintStream streamOut;
    private final InputStream streamIn;
    private final String userName;
    private final Socket client;
    private final String color;
    private String room = "Home";

    public User(Socket client, String name) throws IOException {
        this.client = client;
        this.streamOut = new PrintStream(this.client.getOutputStream());
        this.streamIn = this.client.getInputStream();
        this.userName = name;
        int userId = nbUser;
        this.color = ColorInt.getColor(userId);
        nbUser += 1;
    }

    public PrintStream getOutStream() {
        return this.streamOut;
    }

    public InputStream getInputStream() {
        return this.streamIn;
    }

    public boolean isSocketClosed() {
        return this.client.isClosed();
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUsername() {
        return this.userName;
    }

    public String getColor() {
        return this.color;
    }
}

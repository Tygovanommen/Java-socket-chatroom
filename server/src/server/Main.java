package server;

public class Main {

    // Desired socket port
    private static final int port = Integer.parseInt(new Property().getProperty("port"));

    public static void main(String[] args) {
        Server server = new Server(port);
        server.startServer();
    }
}
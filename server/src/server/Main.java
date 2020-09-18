package server;

public class Main {

    // Desired socket port
    private static final int port = 5000;

    public static void main(String[] args) {
        Server server = new Server(port);
        server.startServer();
    }
}
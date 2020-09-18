package user;

import java.util.Scanner;

public class Main {

    // Desired socket port and host
    private static final int port = 5000;
    private static final String host = "localhost";

    public static void main(String[] args) {
        UserThread userThread = new UserThread(host, port);

        // Set User object
        User user = new User(selectUserName());

        // Start User Thread
        userThread.startUserThread(user);
    }

    /**
     * Request username and let user fill it in
     * @return username
     */
    private static String selectUserName() {
        String userName = null;
        Scanner scan = new Scanner(System.in);
        System.out.println("Username:");
        while (userName == null || userName.trim().equals("")) {
            userName = scan.nextLine();
            if (userName.trim().equals("")) {
                System.out.println("Username can't be empty. Please enter again:");
            }
        }
        return userName;
    }

}
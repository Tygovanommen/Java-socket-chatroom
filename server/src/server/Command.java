package server;

public class Command {

    private String message;
    private final String userInput;
    private boolean isCommand = false;
    private boolean roomChange = false;
    private final Server server;

    public Command(String userInput, Server server) {
        this.userInput = userInput;
        this.server = server;
        this.execute();
    }

    private void execute() {
        // If userinput starts with slash
        if (this.userInput.startsWith("/")) {
            this.isCommand = true;
            switch (this.userInput.split(" ")[0]) {
                case "/users":
                    this.message = "Current online users:";
                    for (User user : this.server.getAllThreads()) {
                        this.message += "\n\t" + user.getUsername() ;
                    }
                    break;
                case "/room":
                    this.roomChange = true;
                    break;
                case "/help":
                case "/info":
                    this.message = "Commands:";
                    this.message += "\n\t/users";
                    this.message += "\n\t/room {room_name}";
                    this.message += "\n\t/help";
                    break;
                default:
                    this.message = "Command does not exists.";
                    break;
            }
        } else {
            this.isCommand = false;
        }
    }

    public boolean isCommand() {
        return this.isCommand;
    }

    public boolean roomChange() {
        return this.roomChange;
    }

    public String getMessage() {
        return this.message;
    }
}

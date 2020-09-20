package server;

public class Command {

    private String message;
    private final String userInput;
    private boolean isCommand = false;
    private boolean roomChange = false;

    public Command(String userInput) {
        this.userInput = userInput;
        this.execute();
    }

    private void execute() {
        // If userinput starts with slash
        if (this.userInput.startsWith("/")) {
            this.isCommand = true;
            switch (this.userInput.split(" ")[0]) {
                case "/room":
                    this.roomChange = true;
                    break;
                case "/help":
                case "/info":
                    this.message = "We can't help you right now.";
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

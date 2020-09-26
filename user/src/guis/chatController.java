package guis;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import user.EmojiReplacer;
import user.UserSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

public class chatController implements Runnable {

    public VBox chatBox;
    public TextField messageInput;

    private final LinkedList<String> newMessages = new LinkedList<>();
    public ScrollPane chatScroll;
    private boolean messageWaiting = false;

    @FXML
    private UserSocket userSocket;

    public void messageSend(ActionEvent e) {
        if (messageInput.getText().length() >= 1) {
            if (this.userSocket.getAccessThread().isAlive()) {
                synchronized (this.newMessages) {
                    this.messageWaiting = true;
                    this.newMessages.push(messageInput.getText());
                }
            }
            messageInput.setText("");
            messageInput.requestFocus();
        }
    }

    @Override
    public void run() {
        try {
            Socket socket = this.userSocket.getSocket();
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), false);
            InputStream serverInStream = socket.getInputStream();
            Scanner serverIn = new Scanner(serverInStream);
            // While socket connection is still active

            while (!socket.isClosed()) {

                // If there is a new message from other users
                if (serverInStream.available() > 0) {
                    if (serverIn.hasNextLine()) {
                        this.appendMessage(serverIn.nextLine());
                        chatBox.heightProperty().addListener(observable -> chatScroll.setVvalue(1D));
                    }
                }

                // If there are new messages from current user
                if (this.messageWaiting) {
                    String nextSend = "";
                    synchronized (this.newMessages) {
                        nextSend = this.newMessages.pop();
                        this.messageWaiting = !this.newMessages.isEmpty();
                    }

                    // Output message
                    JSONObject json = new JSONObject();
                    json.put("message", nextSend);
                    json.put("timezone", TimeZone.getDefault().getID());

                    serverOut.println(json.toString());
                    serverOut.flush();
                }
            }
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Append message to chatbox
     * @param jsonString json string send from server
     * @throws ParseException Json parse execption
     */
    public void appendMessage(String jsonString) throws ParseException {

        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);

        GridPane gridpane = new GridPane();
        GridPane gridpane2 = new GridPane();

        // Add Username
        if (json.get("username") != null) {
            Label name = new Label(json.get("username") + ": ");
            name.getStyleClass().add("username");
            gridpane2.add(name, 1, 0);
        }

        // Add message

        String message = new EmojiReplacer().replaceString((String) json.get("message"));
        gridpane2.add(new Label(message), 2, 0);
        gridpane.add(gridpane2, 1, 0);

        // Add date
        if (json.get("time") != null) {
            Label date = new Label((String) json.get("time"));
            date.getStyleClass().add("date");
            gridpane.add(date, 1, 1);
        }

        // Add pane
        Platform.runLater(() -> {
            chatBox.getChildren().add(gridpane);
        });
    }

    public void setUserSocket(UserSocket userSocket) {
        this.userSocket = userSocket;
    }

}

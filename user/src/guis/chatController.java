package guis;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import user.TextValidator;
import user.Main;
import user.UserSocket;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

public class chatController implements Runnable {

    public VBox chatBox;
    public Label currentRoom;
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
                        this.messageHandler(serverIn.nextLine());
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Append message to chatbox
     *
     * @param jsonString json string send from server
     */
    public void messageHandler(String jsonString) {
        Platform.runLater(() -> {
            try {
                JSONObject json = (JSONObject) new JSONParser().parse(jsonString);

                // Set values
                String username = (String) json.get("username");
                String userColor = (String) json.get("user_color");
                String room = (String) json.get("room");
                String message = (String) json.get("message");
                String time = (String) json.get("time");

                // Set room
                if (room != null) {
                    currentRoom.setText(room);
                    return;
                }

                GridPane gridpane = new GridPane();
                GridPane gridpane2 = new GridPane();

                // Add Username
                if (username != null) {
                    Label name = new Label(username + ": ");
                    name.getStyleClass().add("username");
                    //Set current user color
                    name.setTextFill(Color.web(userColor));
                    gridpane2.add(name, 1, 0);
                }

                // Add message
                TextValidator validator = new TextValidator();
                String messageEmoji = validator.replaceEmoji(message);
                TextArea textArea = new TextArea(messageEmoji);

                double width = (this.computeTextWidth(textArea.getFont(), textArea.getText()) + 10) + 20;
                textArea.setPrefWidth(width);

                int height = (textArea.getText().split("\r\n|\r|\n").length * 17) + 14;
                textArea.setMinHeight(height);
                textArea.setMaxHeight(height);
                textArea.setEditable(false);
                textArea.getStyleClass().add("textArea");

                gridpane2.add(textArea, 2, 0);
                gridpane.add(gridpane2, 1, 0);

                // Add date
                if (time != null) {
                    Label date = new Label(time);
                    date.getStyleClass().add("date");
                    gridpane.add(date, 1, 1);
                }

                // Add pane
                Platform.runLater(() -> {
                    chatBox.getChildren().add(gridpane);
                });

                // Show notification
                if (username != null && !username.equals(this.userSocket.getUserName()) && !Main.stage.isFocused()) {
                    SystemTray tray = SystemTray.getSystemTray();
                    Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // TBD user profile
                    TrayIcon trayIcon = new TrayIcon(image);
                    trayIcon.setImageAutoSize(true);
                    try {
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    trayIcon.displayMessage(username, message, TrayIcon.MessageType.INFO);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private double computeTextWidth(Font font, String text) {
        Text helper = new Text();
        helper.setText(text);
        helper.setFont(font);
        helper.setWrappingWidth(0);
        double w = Math.min(helper.prefWidth(-1), 0.0D);
        helper.setWrappingWidth((int) Math.ceil(w));
        return Math.ceil(helper.getLayoutBounds().getWidth());
    }

    public void setUserSocket(UserSocket userSocket) {
        this.userSocket = userSocket;
    }
}
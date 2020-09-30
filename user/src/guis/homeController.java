package guis;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import user.Main;
import user.Property;
import user.UserSocket;

import java.io.FileReader;
import java.io.IOException;

public class homeController {

    private final UserSocket userSocket;
    public GridPane accounts;
    private String username;

    public TextField usernameInput;

    public homeController() {
        // Desired socket port and host
        Property property = new Property();
        userSocket = new UserSocket(property.getProperty("host"), Integer.parseInt(property.getProperty("port")));
        this.setUserPanels();
    }

    private void setUserPanels() {
        try {
            JSONArray jsonUsers = (JSONArray) new JSONParser().parse(new FileReader("users.json"));
            int i = 0;
            int rowCount = 0;
            GridPane gridPane = new GridPane();
            for (Object userObject : jsonUsers) {
                JSONObject user = (JSONObject) userObject;
                String name = (String) user.get("name");

                Button button = new Button(name);
                button.setOnAction(e -> {
                    this.openProfile(e, name);
                });

                gridPane.add(button, i, rowCount);
                if (i == 2) {
                    i = 0;
                    rowCount++;
                } else {
                    i++;
                }
            }
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setVgap(10);
            gridPane.setHgap(10);

            Platform.runLater(() -> {
                accounts.add(gridPane, 0, 0);
            });
        } catch (java.io.IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void openProfile(ActionEvent e, String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profileScreen.fxml"));
            Parent root = loader.load();
            profileController controller = loader.getController();
            controller.loadUserData(name);
            Scene child2 = new Scene(root, Main.stageWidth, Main.stageHeight);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.setScene(child2);
            window.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void loginAction(ActionEvent e) throws IOException {
        this.username = this.usernameInput.getText();
        if (this.username.length() < 1) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Username can't be empty. Please enter again.");
            errorAlert.showAndWait();
        } else {
            joinServer(e);
        }
    }

    private void joinServer(ActionEvent e) throws IOException {
        // Initiate chat screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatScreen.fxml"));
        Parent root = loader.load();
        chatController controller = loader.getController();
        controller.setUserSocket(userSocket);

        // Wait till connected
        boolean connected = userSocket.connectSocket(this.username, controller);
        if (connected) {
            // Open chat screen
            Scene child2 = new Scene(root, Main.stageWidth, Main.stageHeight);
            child2.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.setScene(child2);
            window.show();
        } else {
            // Show error popup
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Server error");
            errorAlert.setContentText("Unable to connect to server, please try again later.");
            errorAlert.showAndWait();
        }
    }

}

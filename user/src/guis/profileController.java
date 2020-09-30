package guis;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import user.Main;
import user.Property;
import user.UserSocket;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class profileController {

    public ImageView profilePic;
    public Button submitProfile;
    public TextField username;
    public ColorPicker color;
    public ChoiceBox<String> notifications;
    public Button profileUpload;
    public Button loginButton;

    private String name;

    private void showUserData(JSONObject user) {
        this.name = (String) user.get("name");
        String notifications = (String) user.get("notifications");
        this.username.setText(this.name);
        try {
            this.color.setValue(Color.web((String) user.get("color")));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        this.notifications.setItems(FXCollections.observableArrayList("Yes", "No"));
        if (notifications != null) {
            this.notifications.setValue(notifications);
        }
    }

    public void loadUserData(String name) {
        try {
            JSONArray jsonUsers = (JSONArray) new JSONParser().parse(new FileReader("users.json"));
            for (Object userObject : jsonUsers) {
                JSONObject user = (JSONObject) userObject;
                String nameJson = (String) user.get("name");
                if (nameJson.equals(name)) {
                    this.showUserData(user);
                    return;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void submitProfile(ActionEvent a) {
        boolean isSaved = this.saveUserData();
        if (isSaved) {
            // Go back to homescreen
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/guis/homeScreen.fxml"));
                Main.stage.setScene(new Scene(root, Main.stageWidth, Main.stageHeight));
                Main.stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean saveUserData() {
        try {
            JSONArray jsonUsers = (JSONArray) new JSONParser().parse(new FileReader("users.json"));

            // Update JSON
            boolean update = false;
            JSONArray newArray = new JSONArray();
            for (Object userObject : jsonUsers) {
                JSONObject user = (JSONObject) userObject;
                String nameJson = (String) user.get("name");
                if (nameJson.equals(this.name)) {
                    user = this.newUserJson();
                    update = true;
                }
                newArray.add(user);
            }

            // Object not updated, add new value
            if (!update) {
                newArray.add(this.newUserJson());
            }

            // Save to json file
            @SuppressWarnings("resource")
            FileWriter file = new FileWriter("users.json");
            file.write(newArray.toJSONString());
            file.flush();

            return true;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JSONObject newUserJson() {
        JSONObject user = new JSONObject();
        user.put("name", this.username.getText());
        user.put("display_picture", "");
        user.put("color", "#" + Integer.toHexString(this.color.getValue().hashCode()));
        user.put("notifications", this.notifications.getValue());
        return user;
    }

    public void loginButton(ActionEvent a) {
        boolean isSaved = this.saveUserData();
        if (isSaved) {
            try {
                JSONArray jsonUsers = (JSONArray) new JSONParser().parse(new FileReader("users.json"));
                for (Object userObject : jsonUsers) {
                    JSONObject user = (JSONObject) userObject;
                    String nameJson = (String) user.get("name");
                    if (nameJson.equals(name)) {
                        // Start connection
                        joinServer(a, user);
                        return;
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void joinServer(ActionEvent a, JSONObject user) {
        try {
            Property property = new Property();
            UserSocket userSocket = new UserSocket(property.getProperty("host"), Integer.parseInt(property.getProperty("port")));

            // Initiate chat screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatScreen.fxml"));
            Parent root = loader.load();
            chatController controller = loader.getController();
            controller.setUserSocket(userSocket);

            // Wait till connected
            boolean connected = userSocket.connectSocket((String) user.get("name"), controller);
            if (connected) {
                // Open chat screen
                Scene child2 = new Scene(root, Main.stageWidth, Main.stageHeight);
                child2.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
                Stage window = (Stage) ((Node) a.getSource()).getScene().getWindow();
                window.setScene(child2);
                window.show();
            } else {
                // Show error popup
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Server error");
                errorAlert.setContentText("Unable to connect to server, please try again later.");
                errorAlert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

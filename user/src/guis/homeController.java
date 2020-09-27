package guis;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import user.Property;
import user.UserSocket;

import java.io.IOException;

public class homeController {

    // Desired socket port and host
    private final Property property = new Property();

    private final int port = Integer.parseInt(property.getProperty("port"));
    private final String host = property.getProperty("host");
    private final UserSocket userSocket;

    public TextField usernameInput;

    public homeController() {
        userSocket = new UserSocket(host, port);
    }

    public void loginAction(ActionEvent e) throws IOException {
        String username = this.usernameInput.getText();
        if (username.length() < 1) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Username can't be empty. Please enter again.");
            errorAlert.showAndWait();
        } else {
            // Load screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatScreen.fxml"));
            Parent root = loader.load();
            chatController controller = loader.getController();
            controller.setUserSocket(userSocket);
            Scene child2 = new Scene(root, 600, 375);
            child2.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.setScene(child2);

            // Wait till connected
            boolean connected = userSocket.connectSocket(username, controller);
            if (connected) {
                // Open chat screen
                window.show();
            } else {
                System.exit(1);
            }
        }
    }
}

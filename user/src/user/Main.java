package user;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("../guis/homeScreen.fxml"));
        primaryStage.setTitle("Java socket chatroom");
        primaryStage.setScene(new Scene(root, 400, 375));
        primaryStage.show();
    }
}
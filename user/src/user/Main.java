package user;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;
    public static int stageWidth = 600;
    public static int stageHeight = 375;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // Stop java after closing screen
        Platform.setImplicitExit(true);
        stage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });

        // Open home screen
        Parent root = FXMLLoader.load(getClass().getResource("/guis/homeScreen.fxml"));
        primaryStage.setTitle("Java socket chatroom");
        primaryStage.setScene(new Scene(root, stageWidth, stageHeight));
        primaryStage.show();
    }
}
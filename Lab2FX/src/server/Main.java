package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
        primaryStage.setTitle("Messenger server");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getIcons().add(new Image("server/server.png"));
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(450);
        primaryStage.setOnCloseRequest(ServerController.closeEvent);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

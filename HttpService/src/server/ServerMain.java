package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import server.ui.ServerController;

import java.util.Objects;

public class ServerMain extends Application {

    public static Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ui/server.fxml")));
        primaryStage.setTitle("HTTP File manager server");
        primaryStage.setScene(new Scene(root, 350, 100));
        primaryStage.setMinHeight(100);
        primaryStage.setMinWidth(350);
        primaryStage.setOnCloseRequest(ServerController.closeEvent);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

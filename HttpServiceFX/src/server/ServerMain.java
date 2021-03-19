package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import server.ui.ServerController;

public class ServerMain extends Application {

    public static Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("ui/server.fxml"));
        primaryStage.setTitle("HTTP File manager server");
        primaryStage.setScene(new Scene(root, 550, 400));
        primaryStage.getIcons().add(new Image("server/ui/icons/server.png"));
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(650);
        primaryStage.setOnCloseRequest(ServerController.closeEvent);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

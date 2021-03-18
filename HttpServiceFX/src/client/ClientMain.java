package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientMain extends Application {

    public static Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("ui/client.fxml"));
        primaryStage.setTitle("HTTP File manager server");
        primaryStage.setScene(new Scene(root, 550, 400));
        primaryStage.getIcons().add(new Image("client/ui/icons/mainIcon.png"));
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(650);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
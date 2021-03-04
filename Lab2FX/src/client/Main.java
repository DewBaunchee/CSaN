package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getIcons().add(new Image("client/chat.png"));
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(450);
        primaryStage.setOnCloseRequest(ClientController.onCloseRequest);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package client;

import client.ui.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientMain extends Application {

    public static Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ui/client.fxml")));
        primaryStage.setTitle("HTTP File manager server");
        primaryStage.setScene(new Scene(root, 850, 550));
        primaryStage.getIcons().add(new Image("client/ui/icons/mainIcon.png"));
        primaryStage.setMinHeight(550);
        primaryStage.setMinWidth(800);
        primaryStage.setOnCloseRequest(ClientController.closeEvent);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SapperEntryPoint extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sapper.fxml"));
        Parent root = loader.load();
        primaryStage.setOnCloseRequest(((SapperController) loader.getController()).getOnCloseRequest());
        primaryStage.setTitle("Multiplayer sapper");
        primaryStage.getIcons().add(new Image("client/view/images/mine.png"));
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

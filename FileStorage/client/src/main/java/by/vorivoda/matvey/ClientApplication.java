package by.vorivoda.matvey;

import by.vorivoda.matvey.spring.support.AbstractApplicationWithSpring;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
public class ClientApplication extends AbstractApplicationWithSpring {

    public static void switchRoot(Stage stage, ApplicationScene applicationScene) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApplication.class.getResource(applicationScene.getPath()));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image("/view/image/main-icon-box32x32.ico"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        switchRoot(primaryStage, ApplicationScene.LOGIN);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launchApp(ClientApplication.class, args);
    }
}

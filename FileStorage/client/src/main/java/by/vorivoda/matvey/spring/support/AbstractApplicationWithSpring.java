package by.vorivoda.matvey.spring.support;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractApplicationWithSpring extends Application {

    private static String[] savedArgs;
    protected static ConfigurableApplicationContext context;
    private Stage loadingScreen;

    public static ConfigurableApplicationContext getContext() {
        return context;
    }

    @Override
    public void init() throws Exception {
        Platform.runLater(this::showLoadingScene);
        context = SpringApplication.run(getClass(), savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
        Platform.runLater(this::closeLoadingScene);
    }

    private void showLoadingScene() {
        try {
            loadingScreen = new Stage(StageStyle.TRANSPARENT);
            loadingScreen.setTitle("Splash");
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/fxml/loading.fxml")));
            Scene scene = new Scene(root, Color.TRANSPARENT);
            loadingScreen.setScene(scene);
            loadingScreen.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeLoadingScene() {
        loadingScreen.close();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    protected static void launchApp(Class<? extends AbstractApplicationWithSpring> clazz, String[] args) {
        AbstractApplicationWithSpring.savedArgs = args;
        Application.launch(clazz, args);
    }
}

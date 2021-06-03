package by.vorivoda.matvey.spring.support;

import by.vorivoda.matvey.ApplicationScene;
import by.vorivoda.matvey.controller.LoginFXController;
import by.vorivoda.matvey.controller.RegistrationFXController;
import by.vorivoda.matvey.controller.StorageFXController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ConfigurationController {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    // FXML Integration
    @Bean(name = "login")
    public View getLoginView() {
        return loadView(ApplicationScene.LOGIN.getPath());
    }

    @Bean
    public LoginFXController getLoginController() {
        return (LoginFXController) getLoginView().getController();
    }

    @Bean(name = "registration")
    public View getRegistrationView() {
        return loadView(ApplicationScene.REGISTRATION.getPath());
    }

    @Bean
    public RegistrationFXController getRegistrationController() {
        return (RegistrationFXController) getRegistrationView().getController();
    }

    @Bean(name = "main")
    public View getMainView() {
        return loadView(ApplicationScene.STORAGE_MANAGER.getPath());
    }

    @Bean
    public StorageFXController storageFXController() {
        return (StorageFXController) getMainView().getController();
    }

    protected View loadView(String path) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        return new View(loader.getRoot(), loader.getController());
    }

    public static class View {

        private final Parent view;
        private final Object controller;

        public View(Parent view, Object controller) {
            this.view = view;
            this.controller = controller;
        }

        public Parent getView() {
            return view;
        }

        public Object getController() {
            return controller;
        }
    }
}

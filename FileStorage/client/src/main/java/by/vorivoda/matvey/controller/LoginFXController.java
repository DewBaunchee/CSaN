package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.ApplicationScene;
import by.vorivoda.matvey.ClientApplication;
import by.vorivoda.matvey.controller.util.AlertMessage;
import by.vorivoda.matvey.model.IRemoteStorageClient;
import by.vorivoda.matvey.model.RemoteStorageClient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;

public class LoginFXController extends CommonController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField hiddenPasswordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private ImageView openEyeBtn;

    @FXML
    private ImageView closeEyeBtn;

    @FXML
    private Button signInBtn;

    @FXML
    private Button signUpBtn;

    @FXML
    private AnchorPane closeBtn;

    @FXML
    private HBox titleHBox;

    @FXML
    private Label errorLabel;

    private BooleanProperty isPasswordHidden;
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;

    private IRemoteStorageClient storage;

    @FXML
    void initialize() {
        storage = (RemoteStorageClient) ClientApplication.getContext().getBean("StorageClient");
        isPasswordHidden = new SimpleBooleanProperty(true);

        usernameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();

        usernameProperty.bindBidirectional(usernameField.textProperty());
        passwordProperty.bindBidirectional(hiddenPasswordField.textProperty());

        visiblePasswordField.textProperty().bindBidirectional(hiddenPasswordField.textProperty());
        hiddenPasswordField.visibleProperty().bind(isPasswordHidden);
        visiblePasswordField.visibleProperty().bind(isPasswordHidden.not());

        closeEyeBtn.visibleProperty().bind(isPasswordHidden);
        openEyeBtn.visibleProperty().bind(isPasswordHidden.not());

        openEyeBtn.setOnMouseClicked(mouseEvent -> isPasswordHidden.set(true));
        closeEyeBtn.setOnMouseClicked(mouseEvent -> isPasswordHidden.set(false));

        signInBtn.setOnAction(actionEvent -> {
            boolean isUsernameFieldEmpty = usernameProperty.get().length() == 0;
            boolean isPasswordFieldEmpty = passwordProperty.get().length() == 0;
            if (isUsernameFieldEmpty || isPasswordFieldEmpty) {
                errorOccurred("Fill the fields!");
                return;
            }
            cleanError();

            boolean isSigned =  storage.signIn(usernameProperty.get().trim(), passwordProperty.get().trim());
            if(isSigned) {
                try {
                    ClientApplication.switchRoot((Stage) signInBtn.getScene().getWindow(), ApplicationScene.STORAGE_MANAGER);
                } catch (IOException e) {
                    e.printStackTrace();
                    alert("Error when switching scenes.", signInBtn.getScene().getWindow());
                }
            } else {
                errorOccurred("Wrong username or password.");
            }
        });

        signUpBtn.setOnAction(actionEvent -> {
            try {
                ClientApplication.switchRoot((Stage) signUpBtn.getScene().getWindow(), ApplicationScene.REGISTRATION);
            } catch (IOException e) {
                e.printStackTrace();
                alert("Error when switching scenes.", signInBtn.getScene().getWindow());
            }
        });

        EventHandler<KeyEvent> enterEvent = keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) signInBtn.fire();
        };

        usernameField.setOnKeyPressed(enterEvent);
        hiddenPasswordField.setOnKeyPressed(enterEvent);
        visiblePasswordField.setOnKeyPressed(enterEvent);

        setErrorLabel(errorLabel);
        setCloseBtn(closeBtn);
        setWindowGrabber(titleHBox);
    }
}

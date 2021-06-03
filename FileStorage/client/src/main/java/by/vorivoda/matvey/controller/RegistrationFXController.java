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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistrationFXController extends CommonController {

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
    private PasswordField hiddenConfirmationField;

    @FXML
    private TextField visibleConfirmationField;

    @FXML
    private Button signUpBtn;

    @FXML
    private Button signInBtn;

    @FXML
    private AnchorPane closeBtn;

    @FXML
    private HBox titleHBox;

    @FXML
    private Label errorLabel;

    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private StringProperty confirmationProperty;
    private BooleanProperty isPasswordHidden;

    private IRemoteStorageClient storage;

    @FXML
    void initialize() {
        storage = (RemoteStorageClient) ClientApplication.getContext().getBean("StorageClient");
        isPasswordHidden = new SimpleBooleanProperty(true);

        usernameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
        confirmationProperty = new SimpleStringProperty();

        usernameProperty.bindBidirectional(usernameField.textProperty());
        passwordProperty.bindBidirectional(hiddenPasswordField.textProperty());
        confirmationProperty.bindBidirectional(hiddenConfirmationField.textProperty());

        visiblePasswordField.textProperty().bindBidirectional(hiddenPasswordField.textProperty());
        visibleConfirmationField.textProperty().bindBidirectional(hiddenConfirmationField.textProperty());

        visiblePasswordField.visibleProperty().bind(isPasswordHidden.not());
        visibleConfirmationField.visibleProperty().bind(isPasswordHidden.not());
        hiddenConfirmationField.visibleProperty().bind(isPasswordHidden);
        hiddenPasswordField.visibleProperty().bind(isPasswordHidden);

        hiddenPasswordField.styleProperty().bindBidirectional(visiblePasswordField.styleProperty());
        hiddenPasswordField.styleProperty().bindBidirectional(hiddenConfirmationField.styleProperty());
        hiddenPasswordField.styleProperty().bindBidirectional(visibleConfirmationField.styleProperty());

        openEyeBtn.visibleProperty().bind(isPasswordHidden.not());
        closeEyeBtn.visibleProperty().bind(isPasswordHidden);

        openEyeBtn.setOnMouseClicked(mouseEvent -> isPasswordHidden.set(true));
        closeEyeBtn.setOnMouseClicked(mouseEvent -> isPasswordHidden.set(false));

        signUpBtn.setOnAction(actionEvent -> {
            boolean isUsernameFieldEmpty = usernameProperty.get().length() == 0;
            boolean isPasswordFieldEmpty = passwordProperty.get().length() == 0;
            if (isUsernameFieldEmpty || isPasswordFieldEmpty) {
                errorOccurred("Fill the fields!");
                return;
            }
            if (!passwordProperty.get().equals(confirmationProperty.get())) {
                errorOccurred("Passwords are not equal!");
                return;
            }
            cleanError();

            String username = usernameField.getText().trim();
            String password = hiddenPasswordField.getText().trim();
            Map<String, String> errors;
            try {
                errors = storage.registration(username, password);
            } catch (IOException e) {
                alert(e.getMessage(), signUpBtn.getScene().getWindow());
                return;
            }
            // Dewey
            // password
            if (errors.size() == 0) {
                try {
                    if (storage.signIn(username, password)) {
                        ClientApplication.switchRoot((Stage) signUpBtn.getScene().getWindow(), ApplicationScene.STORAGE_MANAGER);
                    } else {
                        ClientApplication.switchRoot((Stage) signUpBtn.getScene().getWindow(), ApplicationScene.LOGIN);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    alert("Error when switching scenes.", signInBtn.getScene().getWindow());
                }
            } else {
                List<TextField> wrongFields = new ArrayList<>();
                if (errors.get("username") != null) wrongFields.add(usernameField);
                if (errors.get("password") != null) wrongFields.add(hiddenPasswordField);
                errorOccurred(errors.get("summary"), wrongFields);
            }
        });

        signInBtn.setOnAction(actionEvent -> {
            try {
                ClientApplication.switchRoot((Stage) signInBtn.getScene().getWindow(), ApplicationScene.LOGIN);
            } catch (IOException e) {
                e.printStackTrace();
                alert("Error when switching scenes.", signInBtn.getScene().getWindow());
            }
        });

        EventHandler<KeyEvent> enterEvent = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) signInBtn.fire();
        };

        usernameField.setOnKeyPressed(enterEvent);
        hiddenPasswordField.setOnKeyPressed(enterEvent);
        visiblePasswordField.setOnKeyPressed(enterEvent);
        hiddenConfirmationField.setOnKeyPressed(enterEvent);
        visibleConfirmationField.setOnKeyPressed(enterEvent);


        setConfirmationCheck(hiddenConfirmationField);
        setConfirmationCheck(visibleConfirmationField);

        setErrorLabel(errorLabel);
        setWindowGrabber(titleHBox);
        setCloseBtn(closeBtn);
    }

    private void setConfirmationCheck(TextField target) {
        confirmationProperty.addListener((observableValue, oldValue, newValue) -> {
            if (passwordProperty.get().equals(newValue)) {
                target.getStyleClass().remove("wrong-field");
            } else {
                if (!target.getStyleClass().contains("wrong-field"))
                    target.getStyleClass().add("wrong-field");
            }
        });

        passwordProperty.addListener((observableValue, oldValue, newValue) -> {
            if (confirmationProperty.get().equals(newValue)) {
                target.getStyleClass().remove("wrong-field");
            } else {
                if (!target.getStyleClass().contains("wrong-field"))
                    target.getStyleClass().add("wrong-field");
            }
        });
    }
}

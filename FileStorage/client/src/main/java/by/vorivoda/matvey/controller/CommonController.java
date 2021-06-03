package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.ApplicationScene;
import by.vorivoda.matvey.ClientApplication;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommonController {

    private double xOffset;
    private double yOffset;

    public void setWindowGrabber(Node windowGrabber) {
        windowGrabber.setOnMousePressed(event -> {
            xOffset = windowGrabber.getScene().getWindow().getX() - event.getScreenX();
            yOffset = windowGrabber.getScene().getWindow().getY() - event.getScreenY();
        });
        windowGrabber.setOnMouseDragged(event -> {
            windowGrabber.getScene().getWindow().setX(event.getScreenX() + xOffset);
            windowGrabber.getScene().getWindow().setY(event.getScreenY() + yOffset);
        });
    }

    public void setCloseBtn(Node closeBtn) {
        closeBtn.setOnMouseClicked(mouseEvent -> ((Stage) closeBtn.getScene().getWindow()).close());
    }

   private Label errorLabel;

    public void setErrorLabel(Label label) {
        errorLabel = label;
        label.setVisible(false);
    }

    public void errorOccurred(String text) {
        errorLabel.setText(text);
        errorLabel.visibleProperty().unbind();
        errorLabel.setVisible(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1500), errorLabel);
        fadeTransition.setDelay(Duration.millis(3000));
        fadeTransition.setOnFinished(actionEvent -> errorLabel.setVisible(false));
        fadeTransition.playFromStart();
    }

    private Map<String, TextField> wrongFields;

    public void errorOccurred(String text, List<TextField> wrongFieldList) {
        wrongFields = new HashMap<>();
        for(TextField current : wrongFieldList) {
            wrongFields.put(current.getText(), current);
            setWrongField(current);
        }

        errorLabel.setText(text);
        errorLabel.setVisible(true);
    }

    public void cleanError() {
        wrongFields = null;
        errorLabel.setVisible(false);
    }

    private void setWrongField(TextField wrongField) {
        wrongField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            boolean hasErrors = false;

            for(String key : wrongFields.keySet()) {
                TextField current = wrongFields.get(key);

                if(current.getText().equals(key)) {
                   current.getStyleClass().remove("wrong-field");
                } else {
                    current.getStyleClass().add("wrong-field");
                    hasErrors = true;
                }
            }

            errorLabel.setVisible(hasErrors);
        });
    }

    public void showModal(StringProperty answer, ApplicationScene modalScene, Window window) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApplication.class.getResource(modalScene.getPath()));
        try {
            Stage stage = new Stage();
            stage.initOwner(window);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(loader.load()));
            answer.bind(((FolderNameRequesterController) loader.getController()).nameProperty());
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alert(String message, Window window) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApplication.class.getResource(ApplicationScene.INFO_ALERT.getPath()));
        try {
            Stage stage = new Stage();
            stage.initOwner(window);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(loader.load()));
            ((MessageController) loader.getController()).setMessage(message);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

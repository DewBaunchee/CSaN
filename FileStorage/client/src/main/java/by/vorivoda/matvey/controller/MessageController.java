package by.vorivoda.matvey.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessageController {

    @FXML
    private Label msgLabel;

    @FXML
    private Button okButton;

    @FXML
    void initialize() {
        okButton.setOnAction(actionEvent -> {
            ((Stage)okButton.getScene().getWindow()).close();
        });
    }

    public void setMessage(String message) {
        msgLabel.setText(message);
    }
}

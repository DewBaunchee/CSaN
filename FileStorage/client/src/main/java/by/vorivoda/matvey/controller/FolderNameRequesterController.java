package by.vorivoda.matvey.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class FolderNameRequesterController {

    @FXML
    private TextField folderName;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    private StringProperty name;

    @FXML
    void initialize() {
        name = new SimpleStringProperty();
        name.bind(folderName.textProperty());
        folderName.setText("New folder");

        cancelBtn.setOnAction(actionEvent -> {
            folderName.setText("");
            ((Stage)cancelBtn.getScene().getWindow()).close();
        });

        createBtn.setOnAction(actionEvent -> {
            ((Stage)cancelBtn.getScene().getWindow()).close();
        });

        folderName.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) createBtn.fire();
            if(keyEvent.getCode() == KeyCode.ESCAPE) cancelBtn.fire();
        });
    }

    public StringProperty nameProperty() {
        return name;
    }
}

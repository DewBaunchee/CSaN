package client.view.newGameDialog;

import sapper.model.FieldSize;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class NewGameDialogController {

    @FXML
    private Button smallBtn;

    @FXML
    private Button mediumBtn;

    @FXML
    private Button largeBtn;

    @FXML
    private TextField bombCount;

    @FXML
    private TextField fieldWidth;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    private NewGameDialogResult result;

    @FXML
    void initialize() {
        createBtn.setDisable(true);

        bombCount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                bombCount.setText(oldValue);
                return;
            }
            createBtn.setDisable(bombCount.getText().length() == 0 || fieldWidth.getText().length() == 0);
        });

        fieldWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                fieldWidth.setText(oldValue);
                return;
            }
            createBtn.setDisable(bombCount.getText().length() == 0 || fieldWidth.getText().length() == 0);
        });

        smallBtn.setOnAction(actionEvent -> {
            result.setBombCount(FieldSize.SMALL.bombCount);
            result.setFieldWidth(FieldSize.SMALL.fieldWidth);
            close();
        });

        mediumBtn.setOnAction(actionEvent -> {
            result.setBombCount(FieldSize.MEDIUM.bombCount);
            result.setFieldWidth(FieldSize.MEDIUM.fieldWidth);
            close();
        });

        largeBtn.setOnAction(actionEvent -> {
            result.setBombCount(FieldSize.LARGE.bombCount);
            result.setFieldWidth(FieldSize.LARGE.fieldWidth);
            close();
        });

        createBtn.setOnAction(actionEvent -> {
            result.setBombCount(Integer.parseInt(bombCount.getText()));
            result.setFieldWidth(Integer.parseInt(fieldWidth.getText()));
            close();
        });

        cancelBtn.setOnAction(actionEvent -> {
            close();
        });
    }

    public void close() {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }

    public void setResult(NewGameDialogResult inResult) {
        if (inResult == null) throw new NullPointerException();
        result = inResult;
    }
}

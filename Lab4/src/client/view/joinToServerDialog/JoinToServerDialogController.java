package client.view.joinToServerDialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JoinToServerDialogController {

    @FXML
    private TextField serverAddress;

    @FXML
    private TextField serverPort;

    @FXML
    private TextField username;

    @FXML
    private Button joinBtn;

    @FXML
    private Button cancelBtn;

    private JoinToServerDialogResult result;

    @FXML
    void initialize() {
        joinBtn.setDisable(true);

        serverAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            checkBtn();
        });

        serverPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                serverPort.setText(oldValue);
            } else {
                checkBtn();
            }
        });

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[_A-Za-z0-9]{0,30}")) {
                username.setText(oldValue);
            } else {
                checkBtn();
            }
        });

        joinBtn.setOnAction(actionEvent -> {
            result.setAddress(serverAddress.getText());
            result.setPort(Integer.parseInt(serverPort.getText()));
            result.setUsername(username.getText());
            result.setIsPresent(true);
            close();
        });

        cancelBtn.setOnAction(actionEvent -> {
            close();
        });
    }

    private void checkBtn() {
        joinBtn.setDisable(serverPort.getText().length() == 0
                || username.getText().length() < 4
                || serverAddress.getText().length() == 0);
    }

    public void close() {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }

    public void setResult(JoinToServerDialogResult inResult) {
        if (inResult == null) throw new NullPointerException();
        result = inResult;
    }
}

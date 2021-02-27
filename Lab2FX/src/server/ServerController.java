package server;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class ServerController {

    @FXML
    private TextField portField;

    @FXML
    private Button startBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea logArea;

    public static MyServer server;
    public static OutputStream outputStream;
    public static boolean isEnabled = false;

    @FXML
    void initialize() {
        startBtn.setOnAction(actionEvent -> {
            if(portField.getText().length() > 0) {
                try {
                    server = new MyServer(Integer.parseInt(portField.getText()), outputStream);
                    statusEnabled();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                alert("Error", "Enter port please.", Alert.AlertType.ERROR);
            }
        });

        stopBtn.setOnAction(actionEvent -> {
            if(isEnabled) {
                try {
                    server.shutdown();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                statusDisabled();
            }
        });

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]{0,5}")) {
               portField.textProperty().set(oldValue);
            }
        });
    }

    private void statusEnabled() {
        statusLabel.setText("Working");
        statusLabel.setTextFill(Color.BLUE);
        isEnabled = true;
    }

    private void statusDisabled() {
        statusLabel.setText("Stopped");
        statusLabel.setTextFill(Color.RED);
        isEnabled = false;
    }

    public void alert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

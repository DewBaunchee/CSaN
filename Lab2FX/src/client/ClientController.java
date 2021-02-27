package client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import server.MyServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public class ClientController {

    @FXML
    private TextField messageField;

    @FXML
    private Button connectBtn;

    @FXML
    private Button disconnectBtn;

    @FXML
    private Button sendBtn;

    @FXML
    private TextArea messagesArea;

    public static EventHandler<WindowEvent> onCloseRequest;
    private static MyClient client;
    private static OutputListener listener;
    private static OutputStream toUser;
    private static InputStream fromUser;

    @FXML
    void initialize() {
        onCloseRequest = windowEvent -> {
            try {
                listener.interrupt();
                client.disconnect();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        };

        sendBtn.setOnAction(actionEvent -> {

        });

        connectBtn.setOnAction(actionEvent -> {
            String ip = dialog("Question", "Enter server name:");
            /*if(!ip.matches("(\\d{1,3}.){3}(\\d{1,3})")) {
                alert("Error", "Wrong IP format.", Alert.AlertType.ERROR);
                return;
            }*/

            String port = dialog("Question", "Enter server port:");
            if (!MyServer.isPortCorrect(Integer.parseInt(port))) {
                alert("Error", "Illegal port.", Alert.AlertType.ERROR);
                return;
            }

            String name = dialog("Question", "Enter name:");
            if (!name.matches("([a-zA-Z_][a-zA-Z_0-9]{2,10})")) {
                alert("Error", "Illegal name.", Alert.AlertType.ERROR);
            }

            try {
                client = new MyClient(ip, Integer.parseInt(port), name, fromUser, toUser);
                listener.start();
                disconnectBtn.setVisible(true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        disconnectBtn.setOnAction(actionEvent -> {
            try {
                client.disconnect();
            } catch (IOException e) {
                alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                System.out.println(e.getMessage());
            }
            disconnectBtn.setVisible(false);
        });

        messageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 255) {
                messageField.textProperty().set(oldValue);
            }
        });
    }

    public String dialog(String title, String question) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(question);
        dialog.getDialogPane().setMinWidth(400);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public void alert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    class OutputListener extends Thread {
        @Override
        public void run() {
            while(!isInterrupted()) {
                String message = "";
                messagesArea.appendText(message);
            }
        }
    }
}

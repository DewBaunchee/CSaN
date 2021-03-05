package client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import server.MyServer;

import java.io.*;
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
    private static ClientListener listener;

    @FXML
    void initialize() {
        onCloseRequest = windowEvent -> {
            if(listener != null) {
                try {
                    listener.interrupt();
                    client.disconnect();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        sendBtn.setOnAction(actionEvent -> {
            if(client != null && client.isAlive() && messageField.getText().length() > 0) {
                client.send(messageField.getText());
            }
            messageField.clear();
        });

        connectBtn.setOnAction(actionEvent -> {
            String ip = dialog("Question", "Enter server name:");
            if(ip == null) return;

            String port = dialog("Question", "Enter server port:");
            if(port == null) return;

            String name = dialog("Question", "Enter name:");
            if(name == null) return;
            if (!name.trim().matches("([a-zA-Z_][a-zA-Z_0-9]{2,10})")) {
                alert("Error", "Illegal name.", Alert.AlertType.ERROR);
                return;
            }

            try {
                PipedInputStream fromC = new PipedInputStream();
                client = new MyClient(ip, Integer.parseInt(port), name.trim(), fromC);
                listener = new ClientListener(new BufferedReader(new InputStreamReader(fromC)));

                disconnectBtn.setVisible(true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        disconnectBtn.setOnAction(actionEvent -> {
            try {
                listener.interrupt();
                client.disconnect();
            } catch (IOException e) {
                alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                System.out.println(e.getMessage());
            }
            disconnectBtn.setVisible(false);
        });

        messageField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                if(client != null && client.isAlive() && messageField.getText().length() > 0) {
                    client.send(messageField.getText());
                }
                messageField.clear();
            }
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

    class ClientListener extends Thread {
        private final BufferedReader fromClient; // User read <- write client

        public ClientListener(BufferedReader fromC) {
            fromClient = fromC;
            start();
        }

        @Override
        public void run() {
            try {
                while(!isInterrupted()) {
                    String message = fromClient.readLine();
                    messagesArea.appendText(message + "\n");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

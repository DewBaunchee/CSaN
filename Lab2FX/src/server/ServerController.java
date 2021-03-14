package server;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingDeque;

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

    private static MyServer server;
    private static ServerListener listener;
    public static EventHandler<WindowEvent> closeEvent;

    @FXML
    void initialize() {
        closeEvent = windowEvent -> {
            if(server != null && server.isOpened()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Server still working");
                alert.setContentText("Are you sure to close server?");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    server.shutdown();
                    listener.interrupt();
                } else {
                    windowEvent.consume();
                }
            }
        };

        startBtn.setOnAction(actionEvent -> {
            if(portField.getText().length() > 0) {
                try {
                    BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(10);
                    server = new MyServer(Integer.parseInt(portField.getText()), blockingQueue);
                    listener = new ServerListener(blockingQueue);

                    statusEnabled();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                alert("Error", "Enter port please.", Alert.AlertType.ERROR);
            }
        });

        stopBtn.setOnAction(actionEvent -> {
            if(server != null && server.isOpened()) {
                listener.interrupt();
                server.shutdown();
                statusDisabled();
                listener = null;
                server = null;
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
    }

    private void statusDisabled() {
        statusLabel.setText("Stopped");
        statusLabel.setTextFill(Color.RED);
    }

    private void appendLog(String message) {
        logArea.appendText(message);
    }

    public void alert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    class ServerListener extends Thread {
        private final BlockingQueue<String> fromServer; // log read <- write server

        public ServerListener(BlockingQueue<String> fromS) {
            fromServer = fromS;
            start();
        }

        @Override
        public void run() {
            try {
                while(!isInterrupted()) {
                    String message = fromServer.take();
                    appendLog(message + "\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Server listener closed.");
        }
    }
}

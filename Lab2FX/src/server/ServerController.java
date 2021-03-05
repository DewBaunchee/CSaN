package server;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

import java.io.*;
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
                    try {
                        server.shutdown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listener.interrupt();
                } else {
                    windowEvent.consume();
                }
            }
        };

        startBtn.setOnAction(actionEvent -> {
            if(portField.getText().length() > 0) {
                try {
                    PipedInputStream fromServer = new PipedInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(fromServer));

                    PipedOutputStream toLog = new PipedOutputStream();
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(toLog));

                    toLog.connect(fromServer);

                    server = new MyServer(Integer.parseInt(portField.getText()), bf);
                    listener = new ServerListener(br);

                    statusEnabled();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                alert("Error", "Enter port please.", Alert.AlertType.ERROR);
            }
        });

        stopBtn.setOnAction(actionEvent -> {
            if(server != null && server.isOpened()) {
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
    }

    private void statusDisabled() {
        statusLabel.setText("Stopped");
        statusLabel.setTextFill(Color.RED);
    }

    public void alert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    class ServerListener extends Thread {
        private final BufferedReader fromServer; // log read <- write server

        public ServerListener(BufferedReader fromS) {
            fromServer = fromS;
            start();
        }

        @Override
        public void run() {
            try {
                while(!isInterrupted()) {
                    String message = fromServer.readLine();
                    if(message == null) {
                        interrupt();
                    } else {
                        logArea.appendText(message + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server listener closed.");
        }
    }
}

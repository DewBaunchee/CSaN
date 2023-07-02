package server.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;
import server.file.manager.Server;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import server.ServerMain;

import java.io.*;

public class ServerController {

    @FXML
    private TextField portField;

    @FXML
    private Button startStopBtn;

    @FXML
    private Button setStorageBtn;

    private Server server;
    private Thread statusChecker;
    public static EventHandler<WindowEvent> closeEvent;

    @FXML
    void initialize() {
        closeEvent = windowEvent -> {
            if (server != null) server.shutdown();
            if (statusChecker != null) statusChecker.interrupt();
        };

        statusChecker = new Thread(() -> {
            boolean isStatusStopped = true;
            while (!statusChecker.isInterrupted()) {
                if (server != null && server.isAlive()) {
                    if (isStatusStopped) {
                        Platform.runLater(this::statusEnabled);
                        isStatusStopped = false;
                    }
                } else {
                    if (!isStatusStopped) {
                        Platform.runLater(this::statusDisabled);
                        isStatusStopped = true;
                    }
                }
            }
        });
        statusChecker.start();

        EventHandler<ActionEvent> startStopEvent = event -> {
            if (server != null && !server.isInterrupted()) {
                server.shutdown();
                server = null;
            } else {

                try {
                    server = new Server(Integer.parseInt(portField.getText()),
                            askFolder("Choose storage folder").getAbsolutePath());
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        };

        EventHandler<ActionEvent> setStorageEvent = actionEvent -> {
            File storage = askFolder("Choose storage folder");
            if (storage != null && server != null) {
                server.setStorage(storage.getAbsolutePath());
            }
        };

        startStopBtn.setOnAction(startStopEvent);
        setStorageBtn.setOnAction(setStorageEvent);

        portField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                startStopBtn.fire();
            }
        });

        portField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]{0,5}") || (newValue.length() > 0 && Integer.parseInt(newValue) > 65535)) {
                portField.setText(oldValue);
            } else {
                startStopBtn.setDisable(newValue.length() == 0);
            }
        }));
    }

    public void alert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public File askFolder(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File defaultDirectory = new File(new File("").getAbsolutePath());
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(ServerMain.root.getScene().getWindow());
    }

    public void statusEnabled() {
        startStopBtn.setText("Stop");
    }

    public void statusDisabled() {
        startStopBtn.setText("Start");
    }
}
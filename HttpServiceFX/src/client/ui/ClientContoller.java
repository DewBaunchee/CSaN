package client.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import server.HttpFileManager.HTTPServer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import server.HttpFileManager.MyLogger;
import client.ClientMain;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

public class ClientContoller {

    @FXML
    private TextArea logArea;

    @FXML
    private TextField portField;

    @FXML
    private Button startStopBtn;

    @FXML
    private Button setLogFileBtn;

    @FXML
    private Button setStorageBtn;

    @FXML
    private ImageView stateImg;

    private HTTPServer server;
    private Thread statusChecker;
    private MyLogger logger;
    private LoggerListener listener;

    @FXML
    void initialize() {
        startStopBtn.setDisable(true);
        setStorageBtn.setDisable(true);
        setLogFileBtn.setDisable(true);

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
                    File logFile = askFile("Choose log file");
                    Path path = null;
                    if (logFile != null) {
                        path = logFile.toPath();
                    }

                    if(listener != null) {
                        listener.interrupt();
                    }

                    listener = new LoggerListener();
                    logger = new MyLogger(listener.getOutputStream(), path);
                    listener.start();

                    server = new HTTPServer(Integer.parseInt(portField.getText()),
                            askFolder("Choose storage folder").getAbsolutePath(), logger);
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        };

        EventHandler<ActionEvent> setStorageEvent = actionEvent -> {
            File storage = askFolder("Choose storage folder");
            if(storage != null && server != null) {
                server.setStorage(storage.getAbsolutePath());
            }
        };

        EventHandler<ActionEvent> setLogFileEvent = actionEvent -> {
            File logFile = askFile("Choose log file");
            if(logFile != null && logger != null) {
                logger.setLogFile(logFile.toPath());
            }
        };

        startStopBtn.setOnAction(startStopEvent);
        setStorageBtn.setOnAction(setStorageEvent);
        setLogFileBtn.setOnAction(setLogFileEvent);

        portField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
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

    public File askFolder(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File defaultDirectory = new File(new File("").getAbsolutePath());
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(ClientMain.root.getScene().getWindow());
    }

    public File askFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        File defaultDirectory = new File(new File("").getAbsolutePath());
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showSaveDialog(ClientMain.root.getScene().getWindow());
    }

    public void statusEnabled() {
        stateImg.setImage(new Image("server/ui/icons/serverOn.png"));
        startStopBtn.setText("Stop");
        setStorageBtn.setDisable(false);
        setLogFileBtn.setDisable(false);
    }

    public void statusDisabled() {
        stateImg.setImage(new Image("server/ui/icons/serverOff.png"));
        startStopBtn.setText("Start");
        setStorageBtn.setDisable(true);
        setLogFileBtn.setDisable(true);
    }

    class LoggerListener extends Thread {
        private final PipedOutputStream toListener;
        private final BufferedReader br;

        LoggerListener() throws IOException {
            toListener = new PipedOutputStream();
            PipedInputStream fromLog = new PipedInputStream();
            fromLog.connect(toListener);
            br = new BufferedReader(new InputStreamReader(fromLog));
        }

        public OutputStream getOutputStream() {
            return toListener;
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    if(br.ready()) {
                        String line = br.readLine();
                        Platform.runLater(() -> {
                            logArea.appendText(line + "\n");
                        });
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
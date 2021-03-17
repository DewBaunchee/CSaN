package server.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import server.HttpFileManager.HTTPServer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import server.HttpFileManager.MyLogger;
import server.ServerMain;

import java.io.*;
import java.util.Optional;

public class ServerController {

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

    @FXML
    void initialize() {
        startStopBtn.setDisable(true);

        statusChecker = new Thread(() -> {
            boolean isStatusStopped = true;
            while (!statusChecker.isInterrupted()) {
                if (server != null && server.isAlive()) {
                    if(isStatusStopped) {
                        Platform.runLater(this::serverEnabled);
                        isStatusStopped = false;
                    }
                } else {
                    if(!isStatusStopped) {
                        Platform.runLater(this::serverDisabled);
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
                    if (logger == null) {
                        File logFile = askFile();
                        if (logFile != null)
                            logger = new MyLogger(null, logFile.getAbsoluteFile().toPath());
                    }

                    server = new HTTPServer(Integer.parseInt(portField.getText()), askFolder().getAbsolutePath(), null);
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        };

        startStopBtn.setOnAction(startStopEvent);

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

    public File askFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file storage");
        File defaultDirectory = new File(new File("").getAbsolutePath());
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(ServerMain.root.getScene().getWindow());
    }

    public File askFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose log file");
        File defaultDirectory = new File(new File("").getAbsolutePath());
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showSaveDialog(ServerMain.root.getScene().getWindow());
    }

    public void serverEnabled() {
        stateImg.setImage(new Image("server/ui/icons/serverOn.png"));
        startStopBtn.setText("Stop");
    }

    public void serverDisabled() {
        stateImg.setImage(new Image("server/ui/icons/serverOff.png"));
        startStopBtn.setText("Start");
    }

    class LoggerListener extends Thread {
        private final PipedOutputStream toListener;
        private final PipedInputStream fromLog;

        LoggerListener() throws IOException {
            toListener = new PipedOutputStream();
            fromLog = new PipedInputStream();
            fromLog.connect(toListener);
        }

        public OutputStream getOutputStream() {
            return toListener;
        }

        public InputStream getInputStream() {
            return fromLog;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader()) {

            } catch (IOException e) {

            }
        }
    }
}

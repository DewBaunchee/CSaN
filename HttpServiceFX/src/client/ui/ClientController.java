package client.ui;

import client.ClientMain;
import client.HTTPRequester.HTTPRequester;
import client.HTTPRequester.MyLogger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

public class ClientController {

    @FXML
    private SplitPane centerSplitPane;

    @FXML
    private Button contentBtn;

    @FXML
    private Button requestBodyBtn;

    @FXML
    private AnchorPane requestBodyPane;

    @FXML
    private RadioButton enterTextRB;

    @FXML
    private ToggleGroup bodyEnterWay;

    @FXML
    private RadioButton chooseFileRB;

    @FXML
    private TextArea bodyTextArea;

    @FXML
    private HBox chooseFileHBox;

    @FXML
    private Button chooseFileBtn;

    @FXML
    private TextField fileField;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private AnchorPane logAnchorPane;

    @FXML
    private Button closeLogBtn;

    @FXML
    private TextArea logArea;

    @FXML
    private TreeView<SplitPane> filesTreeView;

    @FXML
    private TextField URLField;

    @FXML
    private Button sendBtn;

    @FXML
    private ChoiceBox<String> methodChoice;

    private MyLogger logger;
    private LoggerListener listener;

    @FXML
    void initialize() {
        try {
            listener = new LoggerListener();
            logger = new MyLogger(listener.getOutputStream(), null);
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logArea.setEditable(false);

        methodChoice.getItems().add("GET");
        methodChoice.getItems().add("PUT");
        methodChoice.getItems().add("POST");
        methodChoice.getItems().add("DELETE");
        methodChoice.getItems().add("COPY");
        methodChoice.getItems().add("MOVE");
        methodChoice.getItems().add("VIEW");
        methodChoice.getItems().add("HELP");

        methodChoice.setValue("HELP");

        EventHandler<ActionEvent> contentBtnEvent = actionEvent -> {
            contentPane.setVisible(true);
            requestBodyPane.setVisible(false);

            requestBodyBtn.setOpacity(0.6);
            contentBtn.setOpacity(1);
        };

        EventHandler<ActionEvent> requestBodyBtnEvent = actionEvent -> {
            contentPane.setVisible(false);
            requestBodyPane.setVisible(true);

            requestBodyBtn.setOpacity(1);
            contentBtn.setOpacity(0.6);
        };

        EventHandler<ActionEvent> sendEvent = actionEvent -> {
            if(URLField.getText().length() > 0) {
                byte[] body = new byte[0];

                if(bodyEnterWay.getSelectedToggle().equals(enterTextRB)) {
                    body = bodyTextArea.getText().getBytes();
                } else {
                    if(bodyEnterWay.getSelectedToggle().equals(chooseFileRB)) {
                        Path bodyFile = Paths.get(fileField.getText());

                        if(Files.exists(bodyFile) && !Files.isDirectory(bodyFile)) {
                            try {
                                body = Files.readAllBytes(bodyFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                logger.log(e.getMessage());
                            }
                        } else {
                            alert("Error", "No such file!", Alert.AlertType.ERROR);
                        }
                    }
                }

                HTTPRequester requester = new HTTPRequester(methodChoice.getValue(), URLField.getText(), body, logger);

            } else {
                alert("Warning", "Enter URL!", Alert.AlertType.WARNING);
            }
        };

        EventHandler<ActionEvent> chooseFileBtnEvent = actionEvent -> {
            File file = askFile("Choose file for body request");
            if(file != null) {
                fileField.setText(file.getAbsolutePath());
            }
        };

        EventHandler<ActionEvent> closeLogEvent = actionEvent -> logAnchorPane.setMaxHeight(28);

        EventHandler<ActionEvent> openLogEvent = actionEvent -> {
            logAnchorPane.setMaxHeight(Double.POSITIVE_INFINITY);
            centerSplitPane.setDividerPositions(0.6);
        };

        bodyEnterWay.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (chooseFileRB.equals(newToggle)) {
                chooseFileHBox.setVisible(true);
                bodyTextArea.setVisible(false);
                return;
            }
            if(enterTextRB.equals(newToggle)) {
                chooseFileHBox.setVisible(false);
                bodyTextArea.setVisible(true);
            }
        });

        logArea.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.doubleValue() < 30) {
                closeLogBtn.setText("Open log");
                closeLogBtn.setOnAction(openLogEvent);
            } else {
                closeLogBtn.setText("Close log");
                closeLogBtn.setOnAction(closeLogEvent);
            }
        });

        chooseFileBtn.setOnAction(chooseFileBtnEvent);
        contentBtn.setOnAction(contentBtnEvent);
        requestBodyBtn.setOnAction(requestBodyBtnEvent);
    }

    public static final HashMap<String, String> iconsPaths = new HashMap<>() {{
        put("folder", "client/ui/icons/folder.png");
        put("css", "client/ui/icons/css.png");
        put("txt", "client/ui/icons/txt.png");
        put("gif", "client/ui/icons/gif.png");
        put("png", "client/ui/icons/png.png");
        put("jpg", "client/ui/icons/jpg.png");
        put("html", "client/ui/icons/html.png");
        put("file", "client/ui/icons/file.png");
    }};

    public TreeItem<AnchorPane> newTreeViewFile(String name, String extension) {
        AnchorPane pane = new AnchorPane();
        String iconPath = iconsPaths.get(extension);
        if(iconPath == null) iconPath = iconsPaths.get("file");
        ImageView img = new ImageView(iconPath);
        img.setFitHeight(30);
        img.setFitWidth(30);

        Label label = new Label(name);
        label.setTextFill(Color.rgb(160, 160, 160));
        label.setFont(Font.font("Arial", 20));

        pane.getChildren().add(img);
        pane.getChildren().add(label);

        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 35.0);

        AnchorPane.setBottomAnchor(img, 0.0);
        AnchorPane.setTopAnchor(img, 0.0);
        AnchorPane.setLeftAnchor(img, 0.0);

        return new TreeItem<>(pane);
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
        logger.log("Alert: \n   Title: " + title + "\n   Content: " + content);
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
                        Platform.runLater(() -> logArea.appendText(line + "\n"));
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
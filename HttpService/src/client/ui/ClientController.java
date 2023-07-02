package client.ui;

import client.ClientMain;
import client.http.Requester;
import client.http.Response;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
    private ScrollPane contentPane;

    @FXML
    private TreeView<AnchorPane> filesTreeView;

    @FXML
    private TextField URLField;

    @FXML
    private Button sendBtn;

    @FXML
    private ChoiceBox<String> methodChoice;

    @FXML
    private Label statusCodeLabel;

    @FXML
    private Label statusTextLabel;

    @FXML
    void initialize() {
        URLField.setText("http://localhost:80");

        methodChoice.getItems().add("GET");
        methodChoice.getItems().add("PUT");
        methodChoice.getItems().add("POST");
        methodChoice.getItems().add("DELETE");
        methodChoice.getItems().add("COPY");
        methodChoice.getItems().add("MOVE");
        methodChoice.getItems().add("VIEW");

        methodChoice.setValue("VIEW");

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
            if (URLField.getText().length() > 0) {
                byte[] body = new byte[0];

                if (bodyEnterWay.getSelectedToggle().equals(enterTextRB)) {
                    body = bodyTextArea.getText().getBytes();
                } else {
                    if (bodyEnterWay.getSelectedToggle().equals(chooseFileRB)) {
                        Path bodyFile = Paths.get(fileField.getText());

                        if (Files.exists(bodyFile) && !Files.isDirectory(bodyFile)) {
                            try {
                                body = Files.readAllBytes(bodyFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                log(e.getMessage());
                            }
                        } else {
                            alert("Error", "No such file!", Alert.AlertType.ERROR);
                        }
                    }
                }

                statusCodeLabel.setText("Status code");
                statusTextLabel.setText("Status text");
                setStatusLabelsColor(0);
                Requester requester = new Requester(methodChoice.getValue(), URLField.getText(), body);
                interpretResponse(requester.handle());
            } else {
                alert("Warning", "Enter URL!", Alert.AlertType.WARNING);
            }
        };

        EventHandler<ActionEvent> chooseFileBtnEvent = actionEvent -> {
            File file = askFile("Choose file for body request", false);
            if (file != null) {
                fileField.setText(file.getAbsolutePath());
            }
        };

        filesTreeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY
                    && mouseEvent.getClickCount() == 2) {
                TreeItem<AnchorPane> item = filesTreeView.getSelectionModel().getSelectedItem();
                if (item == null) return;
                if (item.getChildren().size() == 0) {
                    methodChoice.setValue("GET");
                    URLField.setText("http://" + reconstructPath(item));
                    sendBtn.fire();
                }
            }
        });

        URLField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) sendBtn.fire();
        });

        bodyEnterWay.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (chooseFileRB.equals(newToggle)) {
                chooseFileHBox.setVisible(true);
                bodyTextArea.setVisible(false);
                return;
            }
            if (enterTextRB.equals(newToggle)) {
                chooseFileHBox.setVisible(false);
                bodyTextArea.setVisible(true);
            }
        });

        sendBtn.setOnAction(sendEvent);
        chooseFileBtn.setOnAction(chooseFileBtnEvent);
        contentBtn.setOnAction(contentBtnEvent);
        requestBodyBtn.setOnAction(requestBodyBtnEvent);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private String reconstructPath(TreeItem<AnchorPane> item) {
        StringBuilder sb = new StringBuilder();
        TreeItem<AnchorPane> current = item;
        while (current != null) {
            sb.insert(0, getTreeViewText(current));
            current = current.getParent();
        }
        return sb.toString();
    }

    private String getTreeViewText(TreeItem<AnchorPane> item) {
        return ((Label) item.getValue().getChildren().get(1)).getText();
    }

    private void setStatusLabelsColor(int statusCode) {
        switch (statusCode / 100) {
            case 2:
                statusCodeLabel.setTextFill(Color.rgb(0, 255, 0));
                statusTextLabel.setTextFill(Color.rgb(0, 255, 0));
                break;
            case 4:
                statusCodeLabel.setTextFill(Color.rgb(230, 0, 0));
                statusTextLabel.setTextFill(Color.rgb(230, 0, 0));
                break;
            case 5:
                statusCodeLabel.setTextFill(Color.rgb(255, 219, 0));
                statusTextLabel.setTextFill(Color.rgb(255, 219, 0));
                break;
            default:
                statusCodeLabel.setTextFill(Color.rgb(0, 0, 0));
                statusTextLabel.setTextFill(Color.rgb(0, 0, 0));
        }
    }

    public TreeItem<AnchorPane> newTreeViewFile(String name, String extension) {
        AnchorPane pane = new AnchorPane();

        Label label = new Label(name);
        label.setFont(Font.font("Arial", 20));

        pane.getChildren().add(label);

        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 35.0);

        return new TreeItem<>(pane);
    }

    public void alert(String title, String content, Alert.AlertType type) {
        log("Alert: \n   Title: " + title + "\n   Content: " + content);
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public File askFile(String title, boolean isSave) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        File defaultDirectory = new File(new File("").getAbsolutePath());
        chooser.setInitialDirectory(defaultDirectory);
        if (isSave) {
            return chooser.showSaveDialog(ClientMain.root.getScene().getWindow());
        } else {
            return chooser.showOpenDialog(ClientMain.root.getScene().getWindow());
        }
    }

    public void interpretResponse(Response response) {
        if (response == null) {
            alert("Error", "Unknown error during connection.", Alert.AlertType.ERROR);
            return;
        }

        statusCodeLabel.setText(response.getStatusCode() + "");
        statusTextLabel.setText(response.getStatusText());
        setStatusLabelsColor(response.getStatusCode());

        if (response.getContentType().equals("text/plain")
                || response.getContentType().equals("application/json")) {
            log("Response body: \n" + new String(response.getBody(), StandardCharsets.UTF_8) + "\n----End of response body----");
        }

        interpretMethod(response);
    }

    private void interpretMethod(Response response) {
        contentPane.setContent(null);
        switch (response.getMethod()) {
            case "PUT":
            case "DELETE":
            case "COPY":
            case "MOVE":
                Requester requester = new Requester("VIEW", getHost(URLField.getText()) + "/", new byte[]{});
                interpretResponse(requester.handle());
            case "HELP":
            case "POST":
                Label label = new Label(new String(response.getBody(), StandardCharsets.UTF_8));
                label.setFont(Font.font("Arial", 18));
                contentPane.setContent(label);
                break;
            case "GET":
                fillContentPane(response.getContentType(), response.getBody());
                break;
            case "VIEW":
                fillFileBrowser(response.getHost(), response.getPort(), response.getBody());
        }
    }

    private String getHost(String url) {
        int indexSlash = url.indexOf("/", "http://".length());
        return indexSlash == -1 ? url : url.substring(0, indexSlash);
    }

    private void fillFileBrowser(String host, int port, byte[] body) {
        String listString = new String(body);
        List<String> paths = new ArrayList<>();

        for(int i = 0; i < listString.length(); i++) {
            if(listString.charAt(i) == '"') {
                int j = i + 1;
                while(listString.charAt(j) != '"') j++;
                paths.add(listString.substring(i + 1, j));
                i = j + 1;
            }
        }

        filesTreeView.setRoot(recFill(paths, "/"));
        ((Label) filesTreeView.getRoot().getValue().getChildren().get(0)).setText(host + ":" + port + "/");
    }

    private TreeItem<AnchorPane> recFill(List<String> paths, String current) {
        TreeItem<AnchorPane> currentItem;
        if (current.endsWith("/")) {
            String name = current.substring(current.lastIndexOf("/", current.length() - 2) + 1);
            currentItem = newTreeViewFile(name, "folder");

            for (String path : paths) {
                if (isDirectChild(current, path)) {
                    currentItem.getChildren().add(recFill(paths, path));
                }
            }
        } else {
            String name = current.substring(current.lastIndexOf("/") + 1);
            String extension = name.contains(".") ? name.substring(name.indexOf(".") + 1) : "";
            currentItem = newTreeViewFile(name, extension);
        }

        return currentItem;
    }

    private boolean isDirectChild(String parent, String child) {
        child = child.substring(0, child.length() - 1);
        if (child.startsWith(parent)) {
            return child.lastIndexOf("/") == parent.length() - 1;
        }
        return false;
    }

    private void fillContentPane(String type, byte[] content) {
        if (type.contains("image")) {
            ImageView imgView = new ImageView(new Image(new ByteArrayInputStream(content)));
            contentPane.setContent(imgView);
            return;
        }
        if (type.equals("text/html")) {
            WebView web = new WebView();
            web.getEngine().loadContent(new String(content, StandardCharsets.UTF_8));
            contentPane.setContent(web);
            return;
        }
        if (type.contains("text")) {
            Label label = new Label(new String(content, StandardCharsets.UTF_8));
            label.setFont(Font.font("Arial", 16));
            contentPane.setContent(label);
        }
    }
}
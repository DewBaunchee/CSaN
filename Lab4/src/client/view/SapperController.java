package client.view;

import client.model.SapperClient;
import client.view.joinToServerDialog.JoinToServerDialog;
import client.view.joinToServerDialog.JoinToServerDialogResult;
import client.view.newGameDialog.NewGameDialog;
import client.view.newGameDialog.NewGameDialogResult;
import common.SapperUser;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import sapper.model.SapperModel;
import sapper.view.GameZone;
import server.Server;
import server.ServerModel;
import utils.MyLogger;

import java.io.IOException;

public class SapperController {

    @FXML
    private AnchorPane gameZoneContainer;

    @FXML
    private Button newGameBtn;

    @FXML
    private Button createServerBtn;

    @FXML
    private Button stopServerBtn;

    @FXML
    private Button joinToServerBtn;

    @FXML
    private Button serverInfoBtn;

    @FXML
    private Button showStatsBtn;

    private MyLogger logger;
    private Server server;
    private SapperClient adminClient;
    private EventHandler<WindowEvent> onCloseRequest;

    @FXML
    void initialize() {
        onCloseRequest = (event) -> {
            if(adminClient != null && adminClient.isConnected()) {
                adminClient.disconnect();
            }
            if(server != null && server.isWorking()) {
                server.shutdown();
            }
        };

        logger = new MyLogger();

        newGameBtn.setOnAction(actionEvent -> {
            NewGameDialogResult result = new NewGameDialogResult();
            try {
                NewGameDialog dialog = new NewGameDialog(newGameBtn.getScene().getWindow(), result);
                dialog.showAndWait();
                if (result.getBombCount() == 0) return;

                SapperModel model = new SapperModel(result.getBombCount(), result.getFieldWidth());
                if (server != null && server.isWorking()) {
                    server.getModel().setGameZone(new GameZone(model));
                } else {
                    new GameZone(model, gameZoneContainer).addPlayer(new SapperUser("Single player"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        createServerBtn.setOnAction(actionEvent -> {
            String port = askForText("Enter port...");
            if (port == null) return;

            String playersCount = askForText("Enter number of players");
            if (playersCount == null) return;

            String username = askForText("Enter username...");
            if (username == null) return;
            if (username.length() < 4) {
                alert("Error", "Username must have at list 4 symbols", Alert.AlertType.ERROR);
                return;
            }

            if (server != null && server.isWorking()) server.shutdown();
            try {
                server = new Server(new ServerModel(Integer.parseInt(port),
                        Integer.parseInt(playersCount), logger));
                server.start();

                adminClient = new SapperClient(server.getModel().getAddresses().get(0),
                        server.getModel().getPort(), new SapperUser(username), gameZoneContainer);
                adminClient.connect();
            } catch (IOException e) {
                logger.log(e.getMessage());
                alert("Error", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });

        stopServerBtn.setOnAction(actionEvent -> {
            if (server != null) {
                server.shutdown();
            }
        });

        joinToServerBtn.setOnAction(actionEvent -> {
            JoinToServerDialogResult result = new JoinToServerDialogResult();
            try {
                JoinToServerDialog dialog = new JoinToServerDialog(joinToServerBtn.getScene().getWindow(), result);
                dialog.showAndWait();
                if (result.isPresent()) {
                    if (server != null && server.isWorking()) server.shutdown();
                    adminClient = new SapperClient(result.getAddress(),
                            result.getPort(),
                            new SapperUser(result.getUsername()),
                            gameZoneContainer);
                    adminClient.connect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverInfoBtn.setOnAction(actionEvent -> {
            String info;

            if (server != null && server.isWorking()) {
                info = "Server is running on addresses: " + server.getModel().getAddresses().toString()
                        + "\nPort: " + server.getModel().getPort();
            } else {
                info = "Server is stopped";
            }

            alert("Server info", info, Alert.AlertType.INFORMATION);
        });

        showStatsBtn.setOnAction(actionEvent -> {
            if(adminClient != null) {
                alert("User stats", adminClient.getUser().toString(), Alert.AlertType.INFORMATION);
            }
        });
    }

    public EventHandler<WindowEvent> getOnCloseRequest() {
        return onCloseRequest;
    }

    private void alert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String askForText(String title) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        return dialog.showAndWait().orElse(null);
    }
}

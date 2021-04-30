package client.model;

import common.SapperUser;
import common.messages.Message;
import common.messages.clientServerMessage.*;
import common.messages.sapperMessage.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sapper.view.GameZone;
import sapper.view.SapperView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class SapperClient extends Thread {
    private Socket _socket;
    private ObjectOutputStream _toServer;
    private ObjectInputStream _fromServer;

    private final SapperUser _user;
    private final String _hostAddress;
    private final int _hostPort;
    private boolean _connected;

    private final AnchorPane _container;
    private final HashMap<SapperUser, SapperView> _sapperViews;
    private GameZone _gameZone;

    public SapperClient(String hostAddress, int hostPort, SapperUser user, AnchorPane container) {
        _hostAddress = hostAddress;
        _hostPort = hostPort;
        _user = user;
        _connected = false;
        _container = container;
        _sapperViews = new HashMap<>();
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(_hostAddress, _hostPort)) {
            _connected = true;
            _socket = socket;
            _toServer = new ObjectOutputStream(socket.getOutputStream());
            _fromServer = new ObjectInputStream(socket.getInputStream());
            sendMessage(new ClientConnectedMessage(_user));

            do {
                getNextMessage();
            } while (_connected);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        start();
    }

    public void disconnect() {
        sendMessage(new ClientDisconnectedMessage(_user));
        _connected = false;
        try {
            _socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return _connected;
    }

    private void getNextMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) _fromServer.readObject();
        System.out.println("Sapper client " + (_user == null ? "[unknown]" : _user.getUsername())
                + " got message: \n" + message.toString());

        if (message instanceof ClientServerMessage) {
            if (message instanceof TooMuchPlayersMessage) {
                alertOnContainer("Message from server", message.getMessage(), Alert.AlertType.INFORMATION);

            } else if (message instanceof SuchUserAlreadyOnServerMessage) {
                alertOnContainer("Message from server", message.getMessage(), Alert.AlertType.INFORMATION);

            } else if (message instanceof ClientDisconnectedMessage) {
                Platform.runLater(() -> _sapperViews.get(message.getSender()).lose(0, 0));

            } else if (message instanceof WaitForPlayersMessage) {
                Platform.runLater(() -> {
                    Label label = new Label(message.getMessage());
                    label.setPadding(new Insets(20));

                    _container.getChildren().clear();
                    _container.getChildren().add(label);
                });

            } else if (message instanceof WaitingForNewGameMessage) {
                Platform.runLater(() -> {
                    Label label = new Label(message.getMessage());
                    label.setPadding(new Insets(20));

                    _container.getChildren().clear();
                    _container.getChildren().add(label);
                });
            }

        } else if (message instanceof SapperMessage) {
            if (message instanceof GameZoneMessage) {
                _gameZone = ((GameZoneMessage) message).getGameZone();

                _gameZone.setOnWinAction((user, row, col) -> {
                    if (_user.equals(user)) {
                        _user.addWin();
                        sendMessage(new WinMessage(_user));
                        alertOnContainer("Winner", "You won!", Alert.AlertType.INFORMATION);
                    }
                });
                _gameZone.setOnLoseAction((user, row, col) -> {
                    if (_user.equals(user)) {
                        _user.addLose();
                        sendMessage(new LoseMessage(_user, row, col));
                        alertOnContainer("Winner", "You lose!", Alert.AlertType.INFORMATION);
                    }
                });
                _gameZone.setOnMarkToggledAction((user, row, col) -> {
                    if (_user.equals(user))
                        sendMessage(new MarkToggledMessage(_user, row, col));
                });
                _gameZone.setOnOpenAction((user, row, col) -> {
                    if (_user.equals(user))
                        sendMessage(new CellOpenedMessage(_user, row, col));
                });
                _gameZone.setOnFirstStepAction((user, row, col) -> {
                    if (_user.equals(user))
                        sendMessage(new SapperModelMessage(_user, _gameZone.getSapperModel(user)));
                });

                Platform.runLater(() -> {
                    _container.getChildren().clear();
                    _container.getChildren().add(_gameZone.setParent(_user, _sapperViews));
                });
            } else if (message instanceof SapperModelMessage) {
                _gameZone.setField(message.getSender(), ((SapperModelMessage) message).getModel());
                Platform.runLater(() -> _sapperViews.get(message.getSender()).setSapper(((SapperModelMessage) message).getModel()));

            } else if (message instanceof CellOpenedMessage) {
                CellOpenedMessage concreteMessage = (CellOpenedMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(() -> target.openCell(concreteMessage.getRow(), concreteMessage.getCol()));

            } else if (message instanceof MarkToggledMessage) {
                MarkToggledMessage concreteMessage = (MarkToggledMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(() -> target.toggleMark(concreteMessage.getRow(), concreteMessage.getCol()));

            } else if (message instanceof WinMessage) {
                WinMessage concreteMessage = (WinMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(target::win);

                if(_user.equals(message.getSender())) {
                    _user.addWin();
                } else {
                    _user.addLose();
                }
                alertOnContainer("Winner", message.getSender().getUsername() + " won!", Alert.AlertType.INFORMATION);

            } else if (message instanceof LoseMessage) {
                LoseMessage concreteMessage = (LoseMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(() -> target.lose(concreteMessage.getRow(), concreteMessage.getCol()));

            }
        }
    }

    private void alertOnContainer(String title, String text, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(text);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    private void sendMessage(Message message) {
        System.out.println("Sapper client is sending message: \n" + message);
        try {
            _toServer.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SapperUser getUser() {
        return _user;
    }
}

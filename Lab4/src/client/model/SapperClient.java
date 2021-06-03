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
    private Socket _socket; // Сокет клиента
    private ObjectOutputStream _toServer;
    private ObjectInputStream _fromServer;

    private final SapperUser _user; // Объект пользователя клиента
    private final String _hostAddress; // Адрес сервера, если он запущен на этом клиенте
    private final int _hostPort; // Порт
    private boolean _connected; // Состояние подключения

    private final AnchorPane _container; // Контейнер для игровой зоны
    private final HashMap<SapperUser, SapperView> _sapperViews; // Поля связанные с пользователями
    private GameZone _gameZone; // Игровая зона

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
        try (Socket socket = new Socket(_hostAddress, _hostPort)) { // Соединение
            _connected = true;
            _socket = socket;
            _toServer = new ObjectOutputStream(socket.getOutputStream());
            _fromServer = new ObjectInputStream(socket.getInputStream());
            sendMessage(new ClientConnectedMessage(_user)); // Отправка сообщения о подключении

            do {
                getNextMessage(); // Обработать следущее сообщение
            } while (_connected); // Пока подключено
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void connect() {
        start();
    }

    public void disconnect() { // Отключение
        Platform.runLater(() -> _container.getChildren().clear()); // Очитска контейнера
        _connected = false;
        sendMessage(new ClientDisconnectedMessage(_user)); // отправка сообщения об отключении
        try {
            _socket.close(); // Закрытие сокета
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return _connected;
    }

    private void getNextMessage() throws IOException, ClassNotFoundException { // Обработка сообщения
        Message message = (Message) _fromServer.readObject(); // Чтение
        System.out.println("Sapper client " + (_user == null ? "[unknown]" : _user.getUsername())
                + " got message: \n" + message.toString());

        if (message instanceof ClientServerMessage) { // Клиент серверные сообщения
            if (message instanceof TooMuchPlayersMessage) {
                // Сообщение о заполненности сервера
                alertOnContainer("Message from server", message.getMessage(), Alert.AlertType.INFORMATION);

            } else if (message instanceof SuchUserAlreadyOnServerMessage
            || message instanceof GameIsNotEnded) {
                // Сообщение о присутствии пользователя с таким никнейом на сервере
                alertOnContainer("Message from server", message.getMessage(), Alert.AlertType.INFORMATION);

            } else if (message instanceof ClientDisconnectedMessage) {
                // Сообщение об отключении одного из пользователей
                Platform.runLater(() -> _sapperViews.get(message.getSender()).lose(0, 0));

            } else if (message instanceof WaitForPlayersMessage) {
                // Сообщение об ожидании остальных игроков
                Platform.runLater(() -> {
                    Label label = new Label(message.getMessage());
                    label.setPadding(new Insets(20));

                    _container.getChildren().clear();
                    _container.getChildren().add(label);
                });

            } else if (message instanceof WaitingForNewGameMessage) {
                // Ожидание создании игры
                Platform.runLater(() -> {
                    Label label = new Label(message.getMessage());
                    label.setPadding(new Insets(20));

                    _container.getChildren().clear();
                    _container.getChildren().add(label);
                });
            } else if (message instanceof ServerShutdownMessage) {
                // Сообщение об остановке сервера
                Platform.runLater(() -> _container.getChildren().clear());
                _connected = false;
            }

        } else if (message instanceof SapperMessage) { // Сообщения движка сапёра
            if (message instanceof GameZoneMessage) {
                // Сообщение передающее игровую зону целиком
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
                // Сообщение с одним из полей сапёра
                _gameZone.setField(message.getSender(), ((SapperModelMessage) message).getModel());
                Platform.runLater(() -> _sapperViews.get(message.getSender()).setSapper(((SapperModelMessage) message).getModel()));

            } else if (message instanceof CellOpenedMessage) {
                // Сообщение об открытии клетки
                CellOpenedMessage concreteMessage = (CellOpenedMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(() -> target.openCell(concreteMessage.getRow(), concreteMessage.getCol()));

            } else if (message instanceof MarkToggledMessage) {
                // Сообщение о переключении отметки
                MarkToggledMessage concreteMessage = (MarkToggledMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(() -> target.toggleMark(concreteMessage.getRow(), concreteMessage.getCol()));

            } else if (message instanceof WinMessage) {
                // Сообщение о победе
                WinMessage concreteMessage = (WinMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(target::win);

                if (_user.equals(message.getSender())) {
                    _user.addWin();
                } else {
                    _user.addLose();
                }
                alertOnContainer("Winner", message.getSender().getUsername() + " won!", Alert.AlertType.INFORMATION);

            } else if (message instanceof LoseMessage) {
                // Сообщение о поражении
                LoseMessage concreteMessage = (LoseMessage) message;
                SapperView target = _sapperViews.get(concreteMessage.getSender());
                Platform.runLater(() -> target.lose(concreteMessage.getRow(), concreteMessage.getCol()));

            }
        }
    }

    private void alertOnContainer(String title, String text, Alert.AlertType type) { // Сообщение в ГУИ
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(text);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    private void sendMessage(Message message) { // Отправка сообщения
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

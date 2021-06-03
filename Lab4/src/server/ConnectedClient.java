package server;

import common.SapperUser;
import common.messages.Message;
import common.messages.clientServerMessage.*;
import common.messages.sapperMessage.*;
import utils.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.Collections;

public class ConnectedClient extends Thread {
    private final MyLogger _logger; // Экземпляр логгера
    private final Socket _socket; // Сокет соединения с клиентом
    private final Server _server; // Ссылка на сервер
    private final ObjectOutputStream toClient;
    private final ObjectInputStream fromClient;
    private SapperUser _user; // Описание пользователя

    public ConnectedClient(Socket socket, Server server) throws IOException {
        _server = server;
        _logger = server.getModel().getLogger();
        _socket = socket;
        toClient = new ObjectOutputStream(socket.getOutputStream());
        fromClient = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            // Отправка сообщения об успешном подключении
            sendMessage(new ServerAcceptMessage(null, "You are connected. Server is listening..."));

            // Ожидание следущиего сообщения
            do {
                getNextMessage();
            } while (_socket.isConnected());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            _server.getModel().disconnectClient(this);
        }
    }

    public void disconnect() {
        try {
            _socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNextMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) fromClient.readObject();
        _logger.log("Server got message: \n" + message.toString());

        if (message instanceof ClientServerMessage) {
            if (message instanceof ClientConnectedMessage) {
                // При получении запроса о подключения проверить свободно ли место и проверить наличие игрока с таким же ником
                _user = message.getSender();

                if (_server.getModel().hasSuchUser(_user)) {
                    _logger.log(_user.getUsername() + " such user already in game.");
                    sendMessage(new SuchUserAlreadyOnServerMessage(null, "Such user already on server."));
                    _server.getModel().disconnectClient(this);
                } else if (_server.getModel().getGameZone() != null
                        && !_server.getModel().getGameZone().isEnded()) {
                    _logger.log("Game is not ended.");
                    sendMessage(new GameIsNotEnded(null, "Game is not ended."));
                } else {
                    _logger.log(_user.getUsername() + " is connected.");
                    _server.getModel().addClient(this);
                }
            } else if (message instanceof ClientDisconnectedMessage) {
                // При получении сообщения об отключении - отключить на стороне сервера и освободить ресурсы
                _server.getModel().disconnectClient(this);
            }
        } else if (message instanceof SapperMessage) {
            if (message instanceof WinMessage) {
                // При победе засчитать победившему игроку 1 победу
                _server.getModel().getGameZone().gameEnd();
                _user.addWin();
            } else if (message instanceof LoseMessage) {
                // Засчитать поражение
                _user.addLose();
            }

            if (message instanceof SapperModelMessage) {
                // Установить полученное поле
                _server.getModel().getGameZone().setField(
                        message.getSender(),
                        ((SapperModelMessage) message).getModel());
            }
            // Переотправка полученных сообщений
            _server.sendToAllMessage(message, Collections.singletonList(this));
        }

    }

    public SapperUser getUser() {
        return _user;
    }

    public void setUser(SapperUser _user) {
        this._user = _user;
    }

    public void sendMessage(Message message) {
        _logger.log("Sending message to client " + (_user == null ? "[unknown]" : _user.getUsername()) + ": \n" + message.toString());
        try {
            toClient.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            _server.getModel().disconnectClient(this);
        }
    }
}

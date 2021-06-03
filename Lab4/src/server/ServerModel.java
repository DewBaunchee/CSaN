package server;

import common.SapperUser;
import common.messages.clientServerMessage.*;
import common.messages.sapperMessage.GameZoneMessage;
import sapper.view.GameZone;
import utils.MyLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ServerModel {
    // Регексп IPv4
    private static final String ipv4 = "\\d{0,3}\\.\\d{0,3}\\.\\d{0,3}\\.\\d{0,3}";
    private final MyLogger _logger;

    private Server _server; // Ссылка на сервер
    private final int _port; // Порт
    private final List<String> _addresses; // Возможные адреса

    private final List<ConnectedClient> clients; // Подключенные клиенты
    private GameZone _gameZone; // Игровая зона
    private final int _numberOfPlayers; // Необходимое кол-во игроком
    private boolean isWorking; // Сервер запущен?

    public ServerModel(int port, int numberOfPlayers, MyLogger logger) throws IOException {
        if (numberOfPlayers < 1 || numberOfPlayers > 4)
            throw new IOException("Number of players must be lesser than 4 and bigger than 0");
        if (isPortBusy(port)) throw new IOException("Port is busy");

        _server = null;
        _logger = logger;
        _port = port;
        _numberOfPlayers = numberOfPlayers;
        _addresses = readAddresses();
        clients = new ArrayList<>();
    }

    public void shutdown() { // Отключение сервера
        while (clients.size() > 0) {
            // Отсылка сообщения и отключение пользователя на стороне сервера
            clients.get(0).sendMessage(new ServerShutdownMessage(null, "Server is shutting down."));
            disconnectClient(clients.get(0));
        }
        isWorking = false;
    }

    public void start() {
        isWorking = true;
    }

    public static boolean isPortBusy(int port) { // Проверка занятости порта
        try (ServerSocket ignored = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public void addClient(ConnectedClient newClient) {
        // Добавелние нового клиента
        _logger.log("Adding new client");
        if (clients.size() == _numberOfPlayers) {
            _logger.log("Too much players");
            newClient.sendMessage(new TooMuchPlayersMessage(null, "Sorry. There is too much players on the server."));
            return;
        }

        _logger.log("Client added.");
        clients.add(newClient);
        if (_gameZone != null)
            _gameZone.addPlayer(newClient.getUser());
        checkStatus();
    }

    private void checkStatus() {
        // Проверка статуса сервера: ожидание игроков, ожидание создания игры, отправка игровой зоны
        if (_numberOfPlayers == clients.size()) {
            if (_gameZone != null) {
                _gameZone.gameStart();
                _server.sendToAllMessage(new GameZoneMessage(_gameZone.copy()));
            } else {
                _server.sendToAllMessage(new WaitingForNewGameMessage(null, "Wait while admin creating a new game."));
            }
        } else {
            if (_gameZone == null || _gameZone.isEnded())
                _server.sendToAllMessage(new WaitForPlayersMessage(null,
                        "Players " + clients.size() + "/" + _numberOfPlayers));
        }
    }

    public void disconnectClient(ConnectedClient client) {
        // Отключение клиента на стороне сервера
        _logger.log("Disconnecting " + client.getUser().getUsername() + "...");
        clients.remove(client);
        if (_gameZone != null)
            _gameZone.removePlayer(client.getUser());
        _server.sendToAllMessage(new ClientDisconnectedMessage(client.getUser()));
        client.disconnect();
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setGameZone(GameZone gameZone) {
        _gameZone = gameZone;
        for (ConnectedClient client : clients)
            _gameZone.addPlayer(client.getUser());
        checkStatus();
    }

    public void setServer(Server server) {
        _server = server;
    }

    public int getPort() {
        return _port;
    }

    public List<String> getAddresses() {
        return _addresses;
    }

    public List<ConnectedClient> getClients() {
        return clients;
    }

    public MyLogger getLogger() {
        return _logger;
    }

    private ArrayList<String> readAddresses() { // Получение списка адресов для этого сервер
        try {
            ArrayList<String> addresses = new ArrayList<>();
            Enumeration<NetworkInterface> allNI = NetworkInterface.getNetworkInterfaces();

            while (allNI.hasMoreElements()) {
                NetworkInterface ni = allNI.nextElement();
                Enumeration<InetAddress> addrs = ni.getInetAddresses();

                while (addrs.hasMoreElements()) {
                    InetAddress ia = addrs.nextElement();
                    if (ia.getHostAddress().matches(ipv4))
                        addresses.add(ia.getHostAddress());
                }
            }

            _logger.log(addresses.toString());
            return addresses;
        } catch (SocketException e) {
            _logger.log(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public GameZone getGameZone() {
        return _gameZone;
    }

    public boolean hasSuchUser(SapperUser user) {
        for (ConnectedClient client : clients)
            if (user.equals(client.getUser())) return true;
        return false;
    }
}
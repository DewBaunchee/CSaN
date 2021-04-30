package server;

import common.SapperUser;
import common.messages.clientServerMessage.ClientDisconnectedMessage;
import common.messages.clientServerMessage.TooMuchPlayersMessage;
import common.messages.clientServerMessage.WaitForPlayersMessage;
import common.messages.clientServerMessage.WaitingForNewGameMessage;
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
    private static final String ipv4 = "\\d{0,3}\\.\\d{0,3}\\.\\d{0,3}\\.\\d{0,3}";
    private final MyLogger _logger;

    private Server _server;
    private final int _port;
    private final List<String> _addresses;

    private final List<ConnectedClient> clients;
    private GameZone _gameZone;
    private final int _numberOfPlayers;
    private boolean isWorking;

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

    public void shutdown() {
        while(clients.size() > 0) disconnectClient(clients.get(0));
        isWorking = false;
    }

    public void start() {
        isWorking = true;
    }

    public static boolean isPortBusy(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public void addClient(ConnectedClient newClient) {
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
        _logger.log("Disconnecting client...");
        clients.remove(client);
        if(_gameZone != null)
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

    private ArrayList<String> readAddresses() {
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
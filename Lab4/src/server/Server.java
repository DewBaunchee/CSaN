package server;

import client.model.SapperClient;
import common.messages.Message;
import utils.MyLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends Thread {
    private final MyLogger _logger;
    private final ServerModel _model;
    private ServerSocket _serverSocket;

    public Server(ServerModel model) {
        _model = model;
        _model.setServer(this);
        _logger = model.getLogger();
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(_model.getPort())) {
            _serverSocket = server;
            _model.start();
            do {
                Socket socket = server.accept();
                new ConnectedClient(socket, this).start();
            } while (_model.isWorking());
        } catch (IOException e) {
            _logger.log(e.getMessage());
            e.printStackTrace();
            shutdown();
        }
    }

    public void shutdown() {
        _model.shutdown();
        try {
            _serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    public void sendToAllMessage(Message message) {
        for (ConnectedClient client : _model.getClients()) {
            client.sendMessage(message);
        }
    }

    public void sendToAllMessage(Message message, List<ConnectedClient> excluded) {
        for (ConnectedClient client : _model.getClients()) {
            if (excluded != null && !excluded.contains(client))
                client.sendMessage(message);
        }
    }

    public boolean isWorking() {
        return _model.isWorking();
    }

    public ServerModel getModel() {
        return _model;
    }
}

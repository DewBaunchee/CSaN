package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyServer extends Thread {
    private final int port;
    private final BufferedWriter logStream;
    private final ArrayList<ConnectedSocket> connectedClients;
    private ServerSocket serverSocket;

    public MyServer(int inPort, BufferedWriter toLogWriter) throws IOException {
        logStream = toLogWriter;

        if (isPortNotBind(inPort)) {
            port = inPort;
            connectedClients = new ArrayList<>();
            log("Server initialized.");
            start();
        } else {
            log("Illegal port");
            throw new IOException("Illegal port");
        }
    }

    public static boolean isPortNotBind(int inPort) {
        try {
            ServerSocket checkSocket = new ServerSocket(inPort);
            checkSocket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void log(String message) {
        System.out.println(message);

        if (logStream != null) {
            try {
                logStream.write(message + "\n");
                logStream.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean isOpened() {
        return !serverSocket.isClosed();
    }

    @Override
    public void run() {
        log("Starting server...");
        try {
            serverSocket = new ServerSocket(port);
            log("Server started.");

        } catch (IOException e) {
            log("Error during starting: " + e.getMessage());
        }

        log("Starting listening for clients...");
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                connectedClients.add(new ConnectedSocket(socket));

                sendToAll("   Socket connected: " + socket.toString());
            } catch (IOException e) {
                log(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void shutdown() throws IOException {
        for (ConnectedSocket client : connectedClients) {
            if (client.isAlive()) {
                client.send("Server stopped.");
                client.interrupt();
            }
        }
        log("Server stopped.");
        serverSocket.close();
        logStream.close();
        interrupt();
    }

    private void disconnectSocket(ConnectedSocket socket) {
        connectedClients.remove(socket);
        socket.closeSocket();
        log("   Socket disconnected: " + socket.toString());
    }

    public void sendToAll(String message) {
        log(message);
        for (ConnectedSocket client : connectedClients) client.send(message);
    }

    class ConnectedSocket extends Thread {
        private final Socket socket;
        private final BufferedReader fromClient;
        private final BufferedWriter toClient;

        public ConnectedSocket(Socket inSocket) throws IOException {
            socket = inSocket;
            fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            start();
        }

        @Override
        public void run() {
            msgWaiting();
            closeSocket();
        }

        private void closeSocket() {
            if(socket != null && socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Error during closing.");
                    e.printStackTrace();
                }
            }
        }

        private void msgWaiting() {
            try {
                while (!isInterrupted()) {
                    sendToAll(fromClient.readLine());
                }
            } catch (IOException e) {
                log(e.getMessage());
                e.printStackTrace();
                disconnectSocket(this);
                try {
                    fromClient.close();
                    toClient.close();
                    socket.close();
                } catch (IOException ioException) {
                    log(e.getMessage());
                    e.printStackTrace();
                }
            }
            interrupt();
        }

        public void send(String message) {
            try {
                if(socket.isConnected()) {
                    toClient.write(message + "\n");
                    toClient.flush();
                }
            } catch (IOException e) {
                log(e.getMessage());
                e.printStackTrace();
                disconnectSocket(this);
            }
        }
    }
}

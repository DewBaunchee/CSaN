package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyServer extends Thread {
    private int port;
    private BufferedWriter logStream;
    private ArrayList<ConnectedSocket> connectedClients;
    private ServerSocket serverSocket;
    private SocketListener listener;

    public MyServer(int inPort, OutputStream inLog) throws IOException {
        if (isPortCorrect(inPort)) {
            port = inPort;
            logStream = null; //logStream = new BufferedWriter(new OutputStreamWriter(inLog));
            connectedClients = new ArrayList<>();
            log("Server initialized.");
            start();
        } else {
            log("Illegal port");
            throw new IOException("Illegal port");
        }
    }

    public static boolean isPortCorrect(int inPort) {
        try {
            ServerSocket checkSocket = new ServerSocket(inPort);
            checkSocket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void log(String message) {
        if (logStream == null) {
            System.out.println(message);
        } else {
            try {
                logStream.write(message + "\n");
                logStream.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        log("Starting...");
        try {
            serverSocket = new ServerSocket(port);
            listener = new SocketListener();
            log("Started. Starting listening for clients...");
            startListening();
        } catch (IOException e) {
            log("Error during starting: " + e.getMessage());
        }
    }

    private void startListening() throws IOException {
        listener.start();
        log("Listener started.");
    }

    public void shutdown() throws IOException {
        listener.interrupt();
        serverSocket.close();
        for (ConnectedSocket client : connectedClients) {
            client.send("Server stopped.");
            client.interrupt();
        }
        log("Server stopped.");
    }

    class SocketListener extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    connectedClients.add(new ConnectedSocket(socket));
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
        }
    }

    class ConnectedSocket extends Thread {
        private Socket socket;
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
        }

        private void msgWaiting() {
            while (!isInterrupted()) {
                try {
                    sendToAll(fromClient.readLine());
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
        }

        public void sendToAll(String message) {
            for (ConnectedSocket client : connectedClients) client.send(message);
        }

        public void send(String message) {
            try {
                log(message);
                toClient.write(message + "\n");
                toClient.flush();
            } catch (IOException e) {
                log(e.getMessage());
            }
        }
    }
}

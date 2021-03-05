package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class MyServer extends Thread {
    private final int port;
    private final ArrayList<ConnectedSocket> connectedClients;
    private final LogWriter logWriter;
    private ServerSocket serverSocket;

    public MyServer(int inPort, BlockingQueue<String> toLogExchange) throws IOException {
        logWriter = new LogWriter(toLogExchange);

        if (isPortNotBind(inPort)) {
            port = inPort;
            connectedClients = new ArrayList<>();
            logWriter.log("Server initialized.");
            start();
        } else {
            logWriter.log("Illegal port");
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

    public boolean isOpened() {
        return !serverSocket.isClosed();
    }

    @Override
    public void run() {
        logWriter.log("Starting server...");
        try {
            serverSocket = new ServerSocket(port);
            logWriter.log("Server started.");

        } catch (IOException e) {
            logWriter.log("Error during starting: " + e.getMessage());
            return;
        }

        logWriter.log("Starting listening for clients...");
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                connectedClients.add(new ConnectedSocket(socket));

                sendToAll("Socket connected: " + socket.toString());
            } catch (IOException e) {
                logWriter.log("MyServer.run:\n" + e.getMessage());
                e.printStackTrace();
                shutdown();
            }
        }
    }

    public void shutdown() {
        try {
            for (ConnectedSocket client : connectedClients) {
                if (client.isAlive()) {
                    client.send("Server stopped.");
                    client.interrupt();
                }
            }
            logWriter.log("Server stopped.");
            serverSocket.close();
            logWriter.interrupt();
        } catch (IOException e) {
            System.out.println("MyServer.shutdown:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        interrupt();
    }

    private void disconnectSocket(ConnectedSocket socket) {
        connectedClients.remove(socket);
        sendToAll("Socket disconnected: " + socket.toString());
        socket.closeSocket();
    }

    public void sendToAll(String message) {
        logWriter.log(message);
        for (int i = 0; i < connectedClients.size(); i++) {
            ConnectedSocket client = connectedClients.get(i);
            try {
                client.send(message);
            } catch (IOException e) {
                logWriter.log("MyServer.sendToAll:\n" + e.getMessage());
                e.printStackTrace();
                disconnectSocket(client);
                i--;
            }
        }
    }
    
    class LogWriter extends Thread {
        private final BlockingQueue<String> logQueue;
        
        public LogWriter(BlockingQueue<String> queue) {
            logQueue = queue;
            start();
        }
        
        @Override
        public void run() {
            while (!isInterrupted());
            close();
        }

        public void log(String message) {
            System.out.println(message);
                try {
                    logQueue.put(message);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
        }
        
        public void close() {
            System.out.println("Log writer closed.");
        }
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
            disconnectSocket(this);
        }

        private void msgWaiting() {
            try {
                while (!isInterrupted()) {
                    String message = fromClient.readLine();

                    if(message == null) {
                        interrupt();
                    } else {
                        sendToAll(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("ConnectedSocket.run:\n" + e.getMessage());
                e.printStackTrace();
            }
        }

        private void closeSocket() {
            if (socket != null && socket.isConnected()) {
                try {
                    fromClient.close();
                    toClient.close();
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error during closing.");
                    e.printStackTrace();
                }
            }
        }

        public void send(String message) throws IOException {
            if (socket.isConnected()) {
                toClient.write(message + "\n");
                toClient.flush();
            }
        }
    }
}

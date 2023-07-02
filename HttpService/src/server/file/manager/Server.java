package server.file.manager;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

public class Server extends Thread {

    private final int port;
    private String storage;
    private ServerSocket server;

    public Server(int port, String storage) throws IOException {
        log("Checking port " + port + "...");
        if(isPortFree(port)) {
            log("Success: port " + port + " is free.");
            log("Initializing...");
            if(storage != null && storage.length() > 0
                    && Files.exists(Paths.get(storage))
                    && Files.isDirectory(Paths.get(storage))) {
                this.port = port;
                this.storage = storage;
                log("Storage: " + storage);
                log("Success. ");
            } else {
                log("Error: storage \"" + storage + "\" is not found.");
                throw new IOException("Error: storage \"" + storage + "\" is not found.");
            }
        } else {
            log("Error: port " + port + " is not free.");
            throw new IOException("Error: port " + port + " is not free.");
        }
    }

    void log(String message) {
        System.out.println(message);
    }

    public static boolean isPortFree(int port) {
        try {
            ServerSocket test = new ServerSocket(port);
            test.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void setStorage(String storage) {
        log("Setting new storage...");
        if(storage != null && storage.length() > 0
                && Files.exists(Paths.get(storage))
                && Files.isDirectory(Paths.get(storage))) {
            this.storage = storage;
            log("Success. ");
        } else {
            log("Error: storage \"" + storage + "\" is not found.");
        }
    }

    @Override
    public void run() {
        log("Starting server...");
        try {
            log("Name: " + InetAddress.getLocalHost().getHostName());
            log("Port: " + port);
            byte[] localIP = InetAddress.getLocalHost().getAddress();
            StringBuilder sb = new StringBuilder("Server-IP:");
            Enumeration<NetworkInterface> allNI = NetworkInterface.getNetworkInterfaces();

            while(allNI.hasMoreElements())
            {
                NetworkInterface ni = allNI.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    InetAddress ia = addresses.nextElement();
                    byte[] address = ia.getAddress();

                    if(address[0] == localIP[0] || address[1] ==  localIP[1]) {
                        sb.append(" ").append(ia.getHostAddress());
                    }
                }
            }

            log(sb.toString());
        } catch (UnknownHostException | SocketException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
        try {
            server = new ServerSocket(port);
            log("Started. Listening for sockets...");
            while(!isInterrupted()) {
                Socket socket = server.accept();

                ConnectionHandler handler = new ConnectionHandler(socket, storage);
                handler.start();
            }
        } catch (IOException e) {
            shutdown();
        }
        log("Server stopped.");
    }

    public void shutdown() {
        log("Interrupting server...");
        interrupt();
        try {
            log("Closing server socket...");
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }
}
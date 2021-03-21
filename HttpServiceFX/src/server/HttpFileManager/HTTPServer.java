package server.HttpFileManager;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

public class HTTPServer extends Thread {

    private final int port;
    private String storage;
    private final MyLogger logger;
    private ServerSocket server;

    public HTTPServer(int port, String storage, MyLogger inLogger) throws IOException {
        if(inLogger == null) {
            logger = new MyLogger(null, null);
        } else {
            logger = inLogger;
        }

        logger.log("Checking port " + port + "...");
        if(isPortFree(port)) {
            logger.log("Success: port " + port + " is free.");
            logger.log("Initializing...");
            if(storage != null && storage.length() > 0
                    && Files.exists(Paths.get(storage))
                    && Files.isDirectory(Paths.get(storage))) {
                this.port = port;
                this.storage = storage;
                logger.log("Storage: " + storage);
                logger.log("Success. ");
            } else {
                logger.log("Error: storage \"" + storage + "\" is not found.");
                throw new IOException("Error: storage \"" + storage + "\" is not found.");
            }
        } else {
            logger.log("Error: port " + port + " is not free.");
            throw new IOException("Error: port " + port + " is not free.");
        }
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
        logger.log("Setting new storage...");
        if(storage != null && storage.length() > 0
                && Files.exists(Paths.get(storage))
                && Files.isDirectory(Paths.get(storage))) {
            this.storage = storage;
            logger.log("Success. ");
        } else {
            logger.log("Error: storage \"" + storage + "\" is not found.");
        }
    }

    @Override
    public void run() {
        logger.log("Starting server...");
        try {
            logger.log("Name: " + InetAddress.getLocalHost().getHostName());
            logger.log("Port: " + port);
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

            logger.log(sb.toString());
        } catch (UnknownHostException | SocketException e) {
            logger.log(e.getMessage());
            e.printStackTrace();
        }
        try {
            server = new ServerSocket(port);
            logger.log("Started. Listening for sockets...");
            while(!isInterrupted()) {
                Socket socket = server.accept();

                HTTPHandler handler = new HTTPHandler(socket, storage, logger);
                handler.start();
            }
        } catch (IOException e) {
            shutdown();
        }
        logger.log("Server stopped.");
    }

    public void shutdown() {
        logger.log("Interrupting server...");
        interrupt();
        try {
            logger.log("Closing server socket...");
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(e.getMessage());
        }
    }
}
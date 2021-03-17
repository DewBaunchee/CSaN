package server.HttpFileManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTTPServer extends Thread {

    private final int port;
    private final String storage;
    private final MyLogger logger;

    public HTTPServer(int port, String storage) throws IOException {
        logger = new MyLogger(null, Paths.get("src/HttpFileManager/Log.txt"));
        logger.log("Checking port " + port + "...");
        if(isPortFree(port)) {
            logger.log("Success: port " + port + " is free.");
            logger.log("Initializing...");
            if(storage != null && storage.length() > 0
                    && Files.exists(Paths.get(storage))
                    && Files.isDirectory(Paths.get(storage))) {
                this.port = port;
                this.storage = storage;
                logger.log("Success. ");
            } else {
                logger.log("Error: storage \"" + storage + "\" is not found.");
                logger.close();
                throw new IOException("Error: storage \"" + storage + "\" is not found.");
            }
        } else {
            logger.log("Error: port " + port + " is not free.");
            logger.close();
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

    @Override
    public void run() {
        logger.log("Starting server...");
        try (ServerSocket server = new ServerSocket(port)) {
            logger.log("Started. Listening for sockets...");
            while(!isInterrupted()) {
                Socket socket = server.accept();
                HTTPHandler handler = new HTTPHandler(socket, storage, logger);
                handler.start();
            }
        } catch (IOException e) {
            logger.log(getClass() + ".run: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void shutdown() {
        interrupt();
    }
}
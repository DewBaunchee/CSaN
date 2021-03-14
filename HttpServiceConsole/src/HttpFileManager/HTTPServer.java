package HttpFileManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTTPServer extends Thread {

    private int port;
    private String storage;
    private final MyLogger logger;

    public HTTPServer(int port, String storage) throws IOException {
        logger = new MyLogger(null);
        logger.log("Checking port " + port + "...");
        if(isPortFree(port)) {
            logger.log("Success: port " + port + " is free.");
            logger.log("Initializing...");
            if(storage == null || Files.exists(Paths.get(storage))) {
                this.port = port;
                this.storage = storage;
                logger.log("Success. ");
            } else {
                logger.log("Error: storage is not found.");
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

/*
* Создать службу, реализующую удалённое файловое хранилище по протоколу HTTP REST.
* Поддержать методы HTTP со следующей семантикой:
* GET - чтение файла,
* PUT - перезапись файла,
* POST - добавление в конец файла,
* DELETE - удаление файла,
* COPY - копирование файла,
* MOVE - перемещение файла.
*
* Создать программу клиента, демонстрирующую работу службы
 * */
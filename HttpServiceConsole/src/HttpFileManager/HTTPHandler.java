package HttpFileManager;

import java.io.*;
import java.net.Socket;

public class HTTPHandler extends Thread {

    private final Socket socket;
    private final String storage;
    private final MyLogger logger;

    HTTPHandler(Socket socket, String storage, MyLogger logger) {
        this.socket = socket;
        this.storage = storage;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.log("Starting handler for socket: " + socket.toString());
        try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
            String header = getRequestBlock(in);
            if (header.length() == 0) {
                socket.close();
                return;
            }

            String body = getRequestBlock(in);

            logger.log("[" + header + "]");
            logger.log("[" + body + "]");

            sendResponse(out, 200, "OK", "Bugaga");
        } catch (IOException e) {
            logger.log(getClass() + ".run: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, String message) throws IOException {
        PrintStream ps = new PrintStream(out);
        ps.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        ps.printf("Content-Length: %s%n", message.length());
        ps.printf("%n");
        out.write(message.getBytes());
    }

    private String getRequestBlock(InputStream in) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            if(!bf.ready()) return "";
            while ((line = bf.readLine()) != null) {
                if(line.length() == 0) break;
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
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
package server.file.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private final Socket socket;
    private final HttpRequestHandler cmdHandler;

    ConnectionHandler(Socket socket, String storage) {
        this.socket = socket;
        cmdHandler = new HttpRequestHandler(storage);
    }

    @Override
    public void run() {
        log("Starting handler for socket: " + socket.toString());
        try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {

            try {
                String header = getRequestHeader(in);
                byte[] body;
                if (header.contains("Content-Length: ")) {
                    int contentLengthIndex = header.indexOf("Content-Length: ") + "Content-Length: ".length();
                    body = getRequestBody(in,
                            Integer.parseInt(header.substring(contentLengthIndex, header.indexOf("\n", contentLengthIndex))));
                } else {
                    body = new byte[0];
                }
                handle(header, body, out);
            } catch (InvocationTargetException | IllegalAccessException e) {
                cmdHandler.sendResponseHeader(out, 500, "Unknown server error", "text/plain", 0);
            }

        } catch (IOException e) {
            log(getClass() + ".run: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void handle(String header, byte[] body, OutputStream out) throws InvocationTargetException, IllegalAccessException {
        String[] args = header.split(" ");
        Method current = HttpRequestHandler.commands.get(args[0]);
        if (current == null) {
            cmdHandler.sendResponseHeader(out, 501, "Not Implemented", "text/plain", 0);
            return;
        }
        current.invoke(cmdHandler, header, body, out);
    }

    private String getRequestHeader(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();

        char prev = 0;
        while (true) {
            char c = (char) in.read();
            if (c == '\n' && prev == '\n') break;
            if (c == '\r') continue;
            sb.append(c);
            prev = c;
        }

        return sb.toString();
    }

    private byte[] getRequestBody(InputStream in, int length) throws IOException {
        byte[] body = new byte[length];

        int i = 0;
        while (i < length) {
            body[i++] = (byte) in.read();
        }

        return body;
    }
}
package server.HttpFileManager;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPHandler extends Thread {

    private final Socket socket;
    private final MyLogger logger;
    private final HTTPCommandHandler cmdHandler;

    HTTPHandler(Socket socket, String storage, MyLogger logger) {
        this.socket = socket;
        this.logger = logger;
        cmdHandler = new HTTPCommandHandler(storage, logger);
    }

    @Override
    public void run() {
        logger.log("Starting handler for socket: " + socket.toString());
        try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
            String header = getRequestHeader(in);
            int contentLengthIndex = header.indexOf("Content-Length: ") + "Content-Length: ".length();
            byte[] body = getRequestBody(in,
                    Integer.parseInt(header.substring(contentLengthIndex, header.indexOf("\n", contentLengthIndex))));
            handle(header, body, out);
        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
            logger.log(getClass() + ".run: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handle(String header, byte[] body, OutputStream out) throws InvocationTargetException, IllegalAccessException {
        String[] args = header.split(" ");
        Method current = HTTPCommandHandler.commands.get(args[0]);
        if (current == null) {
            cmdHandler.help(header, body, out);
            return;
        }
        current.invoke(cmdHandler, header, body, out);
    }

    private String getRequestHeader(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();

        char prev = 0;
        while (true) {
            char c = (char) in.read();
            if(c == '\n' && prev == '\n') break;
            if(c == '\r') continue;
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
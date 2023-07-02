package client.http;

import java.io.*;
import java.net.Socket;

public class Requester {

    private String method;
    private String url;
    private byte[] body;
    public static final String currentVersion = "1.1";

    public Requester(String method, String url, byte[] body) {
        if (url.startsWith("http://")) {
            log("Initializing requester...");
            log("Method: " + method);
            log("URL: " + url);
            this.method = method;
            this.url = url.substring("http://".length());
            this.body = body;
            log("Initialized.");
        } else {
            log("Error: Not HTTP scheme.");
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    public Response handle() {
        int slashIndex = url.indexOf("/");
        String host = url.substring(0, slashIndex == -1 ? url.length() : slashIndex);
        int port = 80;
        if (host.contains(":")) {
            port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
            host = host.substring(0, host.indexOf(":"));
        }
        String path = slashIndex == -1 ? "/" : url.substring(slashIndex);


        try (Socket socket = new Socket(host, port)) {
            sendRequest(socket.getOutputStream(), path, host, port);
            Response response = getResponse(host, port, socket.getInputStream());
            socket.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            log("Error: " + getClass() + ".handle: " + e.getMessage());
        }

        return null;
    }

    private void sendRequest(OutputStream outputStream, String path, String host, int port) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));
        pw.printf("%s %s HTTP/%s%n", method, path, currentVersion);
        pw.printf("Host: %s%n", port == 80 ? host : host + ":" + port);
        pw.printf("Content-Length: " + body.length);
        pw.printf("%n%n");
        pw.flush();
        outputStream.write(body);
    }

    private Response getResponse(String host, int port, InputStream inputStream) throws IOException {
        String[] firstLine = customReadLine(inputStream).split(" ", 3); // HTTP/1.1 StatusCode StatusText
        int statusCode = Integer.parseInt(firstLine[1]);
        String statusText = firstLine[2];

        String contentType = customReadLine(inputStream); // Content-type
        contentType = contentType.substring(contentType.indexOf(":") + 1).trim();

        String contentLength = customReadLine(inputStream); // Content-Length
        byte[] body = new byte[Integer.parseInt(contentLength.substring(contentLength.indexOf(":") + 2))];

        customReadLine(inputStream);
        for (int i = 0; i < body.length; i++) {
            int s = inputStream.read();
            body[i] = (byte) s;
        }

        return new Response(host, port, method, statusCode, statusText, contentType, body);
    }

    private String customReadLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        char current = (char) in.read();
        while (current != '\r' && current != '\n') {
            sb.append(current);
            current = (char) in.read();
        }
        if (current == '\r') {
            int ignored = in.read();
        }

        return sb.toString();
    }
}
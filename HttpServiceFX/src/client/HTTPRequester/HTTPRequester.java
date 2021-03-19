package client.HTTPRequester;

import java.io.*;
import java.net.Socket;

public class HTTPRequester {

    private MyLogger logger;
    private String method;
    private String url;
    private byte[] body;
    public static final String currentVersion = "1.1";

    public HTTPRequester(String method, String url, byte[] body, MyLogger logger) {
        if(url.startsWith("http://")) {
            logger.log("Initializing requester...");
            logger.log("Method: " + method);
            logger.log("URL: " + url);
            this.method = method;
            this.url = url.substring("http://".length());
            this.body = body;
            this.logger = logger;
            logger.log("Initialized.");
        } else {
            logger.log("Error: Not HTTP scheme.");
        }
    }

    public HTTPResponse handle() {
        int slashIndex = url.indexOf("/");
        String host = url.substring(0, slashIndex == -1 ? url.length() : slashIndex);
        int port = 80;
        if(host.contains(":")) {
            port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
            host = host.substring(0, host.indexOf(":"));
        }
        String path = slashIndex == -1 ? "/" : url.substring(slashIndex);

        
        try (Socket socket = new Socket(host, port)) {
            sendRequest(socket.getOutputStream(), path, host, port);
            HTTPResponse response = getResponse(host, port, socket.getInputStream());
            socket.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            logger.log("Error: " + getClass() + ".handle: " + e.getMessage());
        }

        return null;
    }

    private void sendRequest(OutputStream outputStream, String path, String host, int port) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));
        pw.printf("%s %s HTTP/%s%n", method, path, currentVersion);
        pw.printf("Host: %s%n", port == 80 ? host : host + ":" + port);
        pw.printf("Connection: keep-alive%n");
        pw.printf("Content-Length: " + body.length);
        pw.printf("%n%n");
        pw.flush();
        outputStream.write(body);
    }

    private HTTPResponse getResponse(String host, int port, InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] firstLine = br.readLine().split(" ", 3); // HTTP/1.1 StatusCode StatusText
        int statusCode = Integer.parseInt(firstLine[1]);
        String statusText = firstLine[2];

        String contentType = br.readLine(); // Content-type
        contentType = contentType.substring(contentType.indexOf(":") + 1).trim();

        String contentLength = br.readLine(); // Content-Length
        byte[] body = new byte[Integer.parseInt(contentLength.substring(contentLength.indexOf(":") + 2))];

        int sbxz = br.read();
        int bc = br.read();
        for(int i = 0; i < body.length; i++) {
            int s = br.read(); // TODO Первый байт равен -3, а должно быть -119!
            body[i] = (byte) (0xFF & s);
        }

        return new HTTPResponse(host, port, method, statusCode, statusText, contentType, body);
    }
}
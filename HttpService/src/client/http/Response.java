package client.http;

public class Response {
    private final String host;
    private final int port;
    private final String method;
    private final int statusCode;
    private final String statusText;
    private final String contentType;
    private final byte[] body;

    public Response(String host, int port, String method, int statusCode, String statusText, String contentType, byte[] body) {
        this.host = host;
        this.port = port;
        this.method = method;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.contentType = contentType;
        this.body = body;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getMethod() {
        return method;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }
}

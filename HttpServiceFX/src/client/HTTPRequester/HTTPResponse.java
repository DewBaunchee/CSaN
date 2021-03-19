package client.HTTPRequester;

public class HTTPResponse {
    private int statusCode;
    private String statusText;
    private byte[] body;

    public HTTPResponse(int statusCode, String statusText, byte[] body) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}

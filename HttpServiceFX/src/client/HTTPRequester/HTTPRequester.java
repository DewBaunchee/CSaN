package client.HTTPRequester;

public class HTTPRequester {

    private MyLogger logger;
    private String method;
    private String url;
    private byte[] body;

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
        String host = url.substring(0, url.indexOf("/"));
        int port = 80;
        if(host.contains(":")) {
            port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
        }
        String path = url.substring(url.indexOf("/"));

        return null;
    }
}

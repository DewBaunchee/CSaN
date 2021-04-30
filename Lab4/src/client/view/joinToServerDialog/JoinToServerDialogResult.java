package client.view.joinToServerDialog;

public class JoinToServerDialogResult {
    private String address;
    private int port;
    private String username;
    private boolean isPresent;

    public JoinToServerDialogResult() {
        this.address = "";
        this.port = 0;
        this.username = "";
        isPresent = false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIsPresent(boolean value) {
        isPresent = value;
    }

    public boolean isPresent() {
        return isPresent;
    }
}

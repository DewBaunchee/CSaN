import java.io.*;
import java.net.Socket;
import java.util.Locale;

public class Client {

    private static Socket socket;
    private static BufferedReader in;
    private static BufferedReader fromServer;
    private static BufferedWriter toServer;

    public static void main(String[] args) throws IOException {
        in = new BufferedReader(new InputStreamReader(System.in));
        try {
            try {
                socket = new Socket("localhost", 404);
                System.out.println("Socket is connected.");

                fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                while(true) {
                    System.out.print("Client: ");
                    String message = in.readLine();
                    toServer.write(message + "\n");
                    toServer.flush();

                    System.out.println("Server: " + fromServer.readLine());
                    if(message.toLowerCase(Locale.ROOT).equals("exit")) break;
                }
            } finally {
                System.out.println("Closing socket and streams...");
                socket.close();
                fromServer.close();
                toServer.close();
                System.out.println("Closed.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

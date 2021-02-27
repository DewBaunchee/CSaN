import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private static Scanner in = new Scanner(System.in);
    private static Socket socket;
    private static ServerSocket server;
    private static BufferedReader fromClient;
    private static BufferedWriter toClient;

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("Port:");
                server = new ServerSocket(Integer.parseInt(in.nextLine()));
                System.out.println("Opened");
            } catch (Exception e) {
                System.out.println("Not opened");
            }
        }/*
        try {
            try {
                server = new ServerSocket(404);
                try {
                    System.out.println("Starting server...");
                    socket = server.accept();
                    System.out.println("Started.");

                    fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    while (true) {
                        toClient.write("Hello from server, your message is: " + fromClient.readLine() + "\n");
                        toClient.flush();
                    }
                } finally {
                    System.out.println("Closing socket and streams...");
                    socket.close();
                    fromClient.close();
                    toClient.close();
                    System.out.println("Closed");
                }
            } finally {
                System.out.println("Closing server...");
                server.close();
                System.out.println("Server is closed.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
    }
}

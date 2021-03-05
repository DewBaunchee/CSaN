package client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyClient extends Thread {
    private final Socket clientSocket;
    private final String name;

    private final MsgReader fromServer;
    private final MsgWriter toServer;

    public MyClient(String address, int port, String inName,
                    PipedInputStream toClient) throws IOException {
        System.out.println("Client initializing...");
        name = inName;

        System.out.println("    Starting socket [port = " + port + "]...");
        clientSocket = new Socket(address, port);
        System.out.println("    Socket started.");

        System.out.println("    Creating message reader...");
        fromServer = new MsgReader(clientSocket.getInputStream(), toClient);
        System.out.println("    Message reader created.");

        System.out.println("    Creating message writer...");
        toServer = new MsgWriter(clientSocket.getOutputStream());
        System.out.println("    Message writer created.");
        System.out.println("Client initialized.");
        start();
    }

    @Override
    public void run() {
        System.out.println("Starting client...");
        System.out.println("    Starting message writer...");
        fromServer.start();
        System.out.println("    Message writer started.");

        System.out.println("    Starting message writer...");
        toServer.start();
        System.out.println("    Message writer started.");
        System.out.println("Client started.");
        while (!isInterrupted()) ;

        disconnect();
    }

    public void disconnect() {
        try {
            System.out.println("Closing...");
            fromServer.interrupt();
            toServer.interrupt();
            clientSocket.close();
            System.out.println("Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        toServer.send(message);
    }

    class MsgReader extends Thread {
        private final BufferedReader fromS;
        private final BufferedWriter toU;

        public MsgReader(InputStream fromServer, PipedInputStream toClient) throws IOException {
            fromS = new BufferedReader(new InputStreamReader(fromServer));

            PipedOutputStream toUser = new PipedOutputStream();
            toUser.connect(toClient);
            toU = new BufferedWriter(new OutputStreamWriter(toUser));
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    String message = fromS.readLine();
                    toU.write(message + "\n");
                    toU.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fromS.close();
                toU.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class MsgWriter extends Thread {
        private final BufferedWriter toS;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("[kk:mm:ss]");

        public MsgWriter(OutputStream toServer) {
            toS = new BufferedWriter(new OutputStreamWriter(toServer));
        }

        @Override
        public void run() {
            while (!isInterrupted()) ;
            try {
                toS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String message) {
            try {
                toS.write(dateFormat.format(new Date()) + " | " + name + ": " + message + "\n");
                toS.flush();
            } catch (IOException e) {
                e.printStackTrace();
                    disconnect();
            }
        }
    }
}
package client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyClient extends Thread {
    private Socket clientSocket;
    private final String name;

    private MsgReader fromServer;
    private MsgWriter toServer;

    public MyClient(String address, int port, String inName,
                    InputStream fromUser, OutputStream toUser) throws IOException {
        name = inName;

        clientSocket = new Socket(address, port);
        fromServer = new MsgReader(clientSocket.getInputStream(), toUser);
        toServer = new MsgWriter(clientSocket.getOutputStream(), fromUser);
        start();
    }

    @Override
    public void run() {
        fromServer.start();
        toServer.start();
    }

    public void disconnect() throws IOException {
        fromServer.interrupt();
        toServer.interrupt();
        clientSocket.close();
    }

    class MsgReader extends Thread {
        private final BufferedReader fromS;
        private final BufferedWriter toU;

        public MsgReader(InputStream fromServer, OutputStream toUser) {
            fromS = new BufferedReader(new InputStreamReader(fromServer));
            toU = new BufferedWriter(new OutputStreamWriter(toUser));
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    String message = fromS.readLine();
                    toU.write(message + "\n");
                    toU.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MsgWriter extends Thread {
        private final BufferedReader fromU;
        private final BufferedWriter toS;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("[hh:mm:ss]");

        public MsgWriter(OutputStream toServer, InputStream fromUser) {
            toS = new BufferedWriter(new OutputStreamWriter(toServer));
            fromU = new BufferedReader(new InputStreamReader(fromUser));
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    String message = fromU.readLine();
                    toS.write(dateFormat.format(new Date()) + " | " + name + ": " + message + "\n");
                    toS.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
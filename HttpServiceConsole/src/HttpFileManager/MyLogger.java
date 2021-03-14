package HttpFileManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MyLogger extends Thread {

    private final OutputStream logStream;

    MyLogger(OutputStream logStream) {
        this.logStream = logStream;
    }

    public void close() throws IOException {
        logStream.close();
    }

    public void log(String message) {
        System.out.println(message);
        if(logStream != null) {
            try (PrintStream printer = new PrintStream(logStream)) {
                printer.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
            }
        }
    }
}

package server.HttpFileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MyLogger extends Thread {

    private final OutputStream logStream;
    private final Path logFile;

    public MyLogger(OutputStream logStream, Path logFile) throws IOException {
        this.logStream = logStream;
        if(Files.exists(logFile)) {
            this.logFile = logFile;
            Files.writeString(logFile, "");
        } else {
            this.logFile = null;
        }
    }

    public void close() throws IOException {
        if (logStream != null)
            logStream.close();
    }

    public void log(String message) {
        System.out.println(message);

        if (logStream != null) {
            try (PrintStream printer = new PrintStream(logStream)) {
                printer.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
            }
        }

        if(logFile != null && Files.exists(logFile)) {
            try {
                Files.writeString(logFile, message + "\n", StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
            }
        }
    }
}

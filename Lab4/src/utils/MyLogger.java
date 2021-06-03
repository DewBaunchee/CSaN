package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MyLogger extends Thread {

    private BufferedWriter bw;
    private Path logFile;

    public MyLogger() {}

    public MyLogger(OutputStream logStream, Path logFile) throws IOException {
        if(logStream != null) {
            this.bw = new BufferedWriter(new OutputStreamWriter(logStream));
        } else {
            this.bw = null;
        }

        if(logFile != null && Files.exists(logFile)) {
            this.logFile = logFile;
            Files.write(logFile, new byte[]{});
        } else {
            this.logFile = null;
        }
    }

    public void setLogFile(Path path) {
        logFile = path;
    }

    public void close() {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(String message) {
        System.out.println(message);

        if (bw != null) {
            try {
                bw.write(message + "\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
                try {
                    bw.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                bw = null;
            }
        }

        if(logFile != null && Files.exists(logFile)) {
            try {
                Files.write(logFile, (message + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(getClass() + ".log: " + e.getMessage());
            }
        }
    }
}

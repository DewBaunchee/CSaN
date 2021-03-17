import HttpFileManager.HTTPServer;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        try {
            new HTTPServer(8110, "D:\\University\\KSIS\\HttpServiceConsole\\src\\HttpFileManager\\storage").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

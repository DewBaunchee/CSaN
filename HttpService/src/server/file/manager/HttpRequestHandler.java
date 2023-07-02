package server.file.manager;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequestHandler {

    public final static String NOT_FOUND = "File is not found";
    public final static String UNSUPPORTED_MEDIA_TYPE = "File extension is not supported";
    public final static String DELETE_SUCCESS = "Deleting done successfully";
    public final static String VIEW_START_DIR_ERROR = "Not directory or directory doesn't exist";
    public final static String NOT_FILE_ERROR = "Request appeals to directory - file needed";
    public final static String NO_SUCH_DIRECTORY = "There is no such directory in the path";

    public final static HashMap<String, String> types = new HashMap<>();

    static {
        types.put("jpeg", "image/jpeg");
        types.put("jpg", "image/jpeg");
        types.put("png", "image/png");
        types.put("gif", "image/gif");
        types.put("txt", "text/plain");
        types.put("css", "text/css");
        types.put("html", "text/html");
        types.put("", "text/plain");
    }

    public final static HashMap<String, Method> commands = new HashMap<>();

    static {
        try {
            commands.put("GET", HttpRequestHandler.class.getDeclaredMethod("get", String.class, byte[].class, OutputStream.class));
            commands.put("PUT", HttpRequestHandler.class.getDeclaredMethod("put", String.class, byte[].class, OutputStream.class));
            commands.put("POST", HttpRequestHandler.class.getDeclaredMethod("post", String.class, byte[].class, OutputStream.class));
            commands.put("DELETE", HttpRequestHandler.class.getDeclaredMethod("delete", String.class, byte[].class, OutputStream.class));
            commands.put("COPY", HttpRequestHandler.class.getDeclaredMethod("copy", String.class, byte[].class, OutputStream.class));
            commands.put("MOVE", HttpRequestHandler.class.getDeclaredMethod("move", String.class, byte[].class, OutputStream.class));
            commands.put("VIEW", HttpRequestHandler.class.getDeclaredMethod("view", String.class, byte[].class, OutputStream.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private final String storage;

    public HttpRequestHandler(String storage) {
        this.storage = storage.replace("\\", "/");
    }

    public void get(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling GET method...");
        Path uri = getURI(header);
        String filename = uri.getFileName().toString();
        String extension = filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
        String httpType = types.get(extension);

        if (httpType == null) {
            sendResponse(out, 415, "Unsupported Media Type",
                    "text/plain", UNSUPPORTED_MEDIA_TYPE.getBytes(StandardCharsets.UTF_8));
            log("GET handled\n");
            return;
        }

        if (Files.exists(uri) && !Files.isDirectory(uri)) {
            sendResponse(out, 200, "OK", httpType, Files.readAllBytes(uri));
        } else {
            sendResponse(out, 404, "Not found", "text/plain", NOT_FOUND.getBytes(StandardCharsets.UTF_8));
        }
        log("GET handled\n");
    }

    public void put(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling PUT method...");
        Path uri = getURI(header);

        if (Files.exists(uri)) {
            if (Files.isDirectory(uri)) {
                sendResponse(out, 400, "Bad Request", "text/plain", NOT_FILE_ERROR.getBytes(StandardCharsets.UTF_8));
            } else {
                sendResponse(out, 200, "OK", "text/plain",
                        ("File overwritten on the path: " + Files.write(uri, body).toString()).getBytes(StandardCharsets.UTF_8));
            }
        } else {
            boolean isDirectoryRequested = (storage +
                    header.substring(0, header.indexOf('\n')).split(" ")[1]).endsWith("/");

            createNestedDirectory(uri.getParent());
            if (isDirectoryRequested) {
                Files.createDirectory(uri);
                sendResponse(out, 200, "OK", "text/plain",
                        ("Directory created on the path: " + uri.toString()).getBytes(StandardCharsets.UTF_8));
            } else {
                Files.write(uri, body);
                sendResponse(out, 200, "OK", "text/plain",
                        ("File created on the path: " + Files.write(uri, body).toString()).getBytes(StandardCharsets.UTF_8));
            }
        }

        log("PUT handled\n");
    }

    private static void createNestedDirectory(Path uri) throws IOException {
        if (Files.exists(uri)) return;

        List<Path> notExistingDirectories = new ArrayList<>();
        Path current = uri;
        while (!Files.exists(current)) {
            notExistingDirectories.add(current);
            current = current.getParent();
        }

        for (int i = notExistingDirectories.size() - 1; i > -1; i--) {
            Files.createDirectory(notExistingDirectories.get(i));
        }
    }

    public void post(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling POST method...");
        Path uri = getURI(header);

        if (Files.exists(uri) && !Files.isDirectory(uri)) {
            Files.write(uri, body, StandardOpenOption.APPEND);
        } else {
            sendResponse(out, 404, "Not Found", "text/plain", NOT_FOUND.getBytes(StandardCharsets.UTF_8));
        }

        log("POST handled\n");
    }

    public void delete(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling DELETE method...");
        Path uri = getURI(header);

        if (Files.exists(uri)) {
            if (Files.isDirectory(uri)) {
                deleteDirectory(uri.toString());
            } else {
                Files.delete(uri);
            }
            sendResponse(out, 200, "OK", "text/plain", DELETE_SUCCESS.getBytes(StandardCharsets.UTF_8));
        } else {
            sendResponse(out, 404, "Not found", "text/plain", NOT_FOUND.getBytes(StandardCharsets.UTF_8));
        }

        log("DELETE handled\n");
    }

    public void deleteDirectory(String path) {
        File storageDir = new File(path);
        File[] files = storageDir.listFiles();
        if (files == null) {
            return;
        }

        for (File current : files) {
            if (current.isDirectory()) {
                deleteDirectory(current.getAbsolutePath());
            } else {
                if (current.delete()) {
                    log("File deleted successfully: " + current.getAbsolutePath());
                } else {
                    log("File delete failed :" + current.getAbsolutePath());
                }
            }
        }

        if (storageDir.delete()) {
            log("Directory deleted successfully: " + storageDir.getAbsolutePath());
        } else {
            log("Directory delete failed :" + storageDir.getAbsolutePath());
        }
    }

    public void copy(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling COPY method...");
        Path src = getURI(header);
        Path dest = Paths.get(storage + new String(body));

        if (Files.exists(src) && !Files.isDirectory(src)) {
            if (Files.exists(dest.getParent())) {
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            } else {
                sendResponse(out, 400, "Bad Request", "text/plain",
                        (NO_SUCH_DIRECTORY + ": " + dest.getParent()).getBytes(StandardCharsets.UTF_8));
            }
        } else {
            sendResponse(out, 404, "Not Found", "text/plain", NOT_FOUND.getBytes(StandardCharsets.UTF_8));
        }

        log("COPY handled\n");
    }

    public void move(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling MOVE method...");
        Path src = getURI(header);
        Path dest = Paths.get(storage + new String(body));

        if (Files.exists(src) && !Files.isDirectory(src)) {
            if (Files.exists(dest.getParent())) {
                Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
            } else {
                sendResponse(out, 400, "Bad Request", "text/plain",
                        (NO_SUCH_DIRECTORY + ": " + dest.getParent()).getBytes(StandardCharsets.UTF_8));
            }
        } else {
            sendResponse(out, 404, "Not Found", "text/plain", NOT_FOUND.getBytes(StandardCharsets.UTF_8));
        }

        log("MOVE handled\n");
    }

    public void view(String header, byte[] body, OutputStream out) throws IOException {
        log("Handling VIEW method...");
        Path uri = getURI(header);

        if (!Files.exists(uri) || !Files.isDirectory(uri)) {
            sendResponse(out, 400, "Bad Request", "text/plain", VIEW_START_DIR_ERROR.getBytes(StandardCharsets.UTF_8));
            log("VIEW handled\n");
            return;
        }

        List<String> listOfPaths = new ArrayList<>();
        fillListOfFiles(storage, uri.toString(), listOfPaths, true);

        ObjectMapper mapper = new ObjectMapper();
        sendResponse(out, 200, "OK", "application/json", mapper.writeValueAsBytes(listOfPaths));

        log("VIEW handled\n");
    }

    private static void fillListOfFiles(String root, String currentDir, List<String> list, boolean isCanonicalNeeded) {
        File storageDir = new File(currentDir);
        File[] files = storageDir.listFiles();
        if (files == null) {
            return;
        }

        if (isCanonicalNeeded) {
            list.add(storageDir.getAbsolutePath()
                    .substring(storageDir.getAbsolutePath().indexOf(root) + root.length() + 1)
                    .replace("\\", "/") + "/");
        } else {
            list.add(storageDir.getAbsolutePath() + "/");
        }

        for (File current : files) {
            if (current.isDirectory()) {
                fillListOfFiles(root, current.getAbsolutePath(), list, isCanonicalNeeded);
            } else {
                if (isCanonicalNeeded) {
                    list.add(current.getAbsolutePath()
                            .substring(current.getAbsolutePath().indexOf(root) + root.length() + 1)
                            .replace("\\", "/"));
                } else {
                    list.add(current.getAbsolutePath());
                }
            }
        }
    }

    public Path getURI(String header) {
        return Paths.get(storage + header.substring(0, header.indexOf('\n')).split(" ")[1]);
    }

    void sendResponseHeader(OutputStream out, int statusCode, String statusText, String type, long length) {
        PrintStream ps = new PrintStream(out);
        ps.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        ps.printf("Content-Type: %s%n", type);
        ps.printf("Content-Length: %s%n", length);
        ps.printf("%n");
        ps.flush();
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, String type, byte[] body) throws IOException {
        log("Sending response...\n   Status code: " + statusCode
                + "\n   Status text: " + statusText + "\n   Type: " + type);
        log("Response body:\n   " + (body.length > 130 ?
                new String(Arrays.copyOfRange(body, 0, 128)) + "..." : new String(body)));

        sendResponseHeader(out, statusCode, statusText, type, body.length);
        out.write(body);

        log("Sending success.");
    }

    private void log(String message) {
        System.out.println(message);
    }
}

/*
 * Создать службу, реализующую удалённое файловое хранилище по протоколу HTTP REST.
 * Поддержать методы HTTP со следующей семантикой:
 * GET - чтение файла,
 * PUT - перезапись файла,
 * POST - добавление в конец файла,
 * DELETE - удаление файла,
 * COPY - копирование файла,
 * MOVE - перемещение файла,
 * VIEW - просмотр списка файла.
 *
 * Создать программу клиента, демонстрирующую работу службы
 * */

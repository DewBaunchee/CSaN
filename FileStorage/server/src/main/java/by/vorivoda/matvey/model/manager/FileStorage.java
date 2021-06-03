package by.vorivoda.matvey.model.manager;

import by.vorivoda.matvey.model.GlobalConstants;
import by.vorivoda.matvey.model.manager.exception.NotADirectory;
import by.vorivoda.matvey.model.manager.exception.NotAFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileStorage {

    private static String contextPath = "";
    private String root;
    private final Logger logger = LoggerFactory.getLogger(FileStorage.class);

    public FileStorage(String root) {
        setRoot(root);
    }

    public List<String> getFolderContent(String path) throws IOException {
        logger.info("Getting list of files in folder " + path + "...");
        path = getFullPath(path);

        List<String> answer;
        if (Files.exists(Paths.get(path))) {
            if (Files.isDirectory(Paths.get(path))) {
                String finalPath = path;
                String[] content = new File(path).list();

                if (content == null) {
                    throw new IOException("Unknown error during getting a folder content.");
                } else {
                    answer = Arrays.stream(content).map((element) -> {
                        if(Files.isDirectory(Paths.get(finalPath + "/" + element))) return element + "/";
                        return element;
                    }).toList();
                }
            } else {
                throw new NotADirectory("Requested path does not point to directory.");
            }
        } else {
            throw new NoSuchFileException("No such folder.");
        }

        logger.info("Getting list of files handled");
        return answer;
    }

    public List<String> getAllPaths(String path) throws IOException {
        logger.info("Getting all paths in storage...");
        path = getFullPath(path);
        List<String> answer;
        if (Files.exists(Paths.get(path))) {
            if (Files.isDirectory(Paths.get(path))) {
                 answer = getListOfContent(path, true);
            } else {
                throw new NotADirectory("Requested path does not point to directory.");
            }
        } else {
            throw new NoSuchFileException("No such folder.");
        }
        logger.info("Getting list of all paths handled.");
        return answer;
    }

    public byte[] getFile(String path) throws IOException {
        logger.info("Getting file " + path + "...");
        path = getFullPath(path);

        byte[] answer;
        if (Files.exists(Paths.get(path))) {
            if (Files.isDirectory(Paths.get(path))) {
                throw new NotAFile("Requested path points to directory.");
            } else {
                answer = Files.readAllBytes(Paths.get(path));
            }
        } else {
            throw new NoSuchFileException("No such file.");
        }

        logger.info("Getting file handled");
        return answer;
    }

    public void appendFile(String path, byte[] content) throws IOException {
        logger.info("Appending file " + path + "...");
        path = getFullPath(path);

        if (Files.exists(Paths.get(path))) {
            if (Files.isDirectory(Paths.get(path))) {
                throw new NotAFile("Requested path points to directory.");
            } else {
                Files.write(Paths.get(path), content, StandardOpenOption.APPEND);
            }
        } else {
            throw new NoSuchFileException("No such file.");
        }

        logger.info("Appending handled");
    }

    public void writeFile(String path, byte[] content) throws IOException {
        logger.info("Writing file " + path + "...");
        path = getFullPath(path);

        if (Files.isDirectory(Paths.get(path))) {
            throw new NotAFile("Requested path points to directory.");
        } else {
            createNestedDirectory(Paths.get(path).getParent());
            Files.write(Paths.get(path), content);
        }

        logger.info("Writing handled");
    }

    public void createFolder(String path) throws IOException {
        logger.info("Creating folder " + path + "...");
        path = getFullPath(path);
        createNestedDirectory(Paths.get(path));
        logger.info("Creating folder handled\n");
    }

    public BigInteger sizeOf(String path) {
        logger.info("Calculating size of " + path + "...");
        path = getFullPath(path);

        if (!Files.exists(Paths.get(path))) {
            throw new NoSuchElementException("No such file or directory.");
        }

        BigInteger size = BigInteger.ZERO;
        for (String currentFile : getListOfContent(path, false)
                .stream()
                .filter(element -> !Files.isDirectory(Paths.get(element)))
                .collect(Collectors.toList())) {
            try {
                size = size.add(BigInteger.valueOf(Files.size(Paths.get(currentFile))));
            } catch (IOException ignored) {
            }
        }

        logger.info("Calculated: " + size);
        return size;
    }

    public void delete(String path) throws IOException {
        logger.info("Deleting " + path + "...");
        path = getFullPath(path);
        Path uri = Paths.get(path);

        if (Files.exists(uri)) {
            if (Files.isDirectory(uri)) {
                deleteDirectory(uri.toString());
            } else {
                Files.delete(uri);
            }
        } else {
            throw new NoSuchFileException("No such file or directory.");
        }

        logger.info("Deleting handled\n");
    }

    public void move(String src, String dest) throws IOException {
        logger.info("Moving " + src + " to " + dest + "...");
        src = getFullPath(src);
        dest = getFullPath(dest);
        Path srcURI = Paths.get(src);
        Path destURI = Paths.get(dest);

        if (Files.exists(srcURI)) {
            Files.move(srcURI, destURI, StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new NoSuchFileException("No such file or directory.");
        }

        logger.info("Move handled\n");
    }

    public void copy(String src, String dest) throws IOException {
        logger.info("Copying " + src + " to " + dest + "...");
        src = getFullPath(src);
        dest = getFullPath(dest);
        Path srcURI = Paths.get(src);
        Path destURI = Paths.get(dest);

        if (Files.exists(srcURI)) {
            Files.copy(srcURI, destURI, StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new NoSuchFileException("No such file or directory.");
        }

        logger.info("Copy handled\n");
    }

    public boolean isDirectory(String path) {
        return Files.isDirectory(Paths.get(getFullPath(path)));
    }

    private String getFullPath(String canonical) {
        String absolute = canonical.startsWith(root)
                ? canonical
                : root + (canonical.startsWith("/") ? "" : "/") + canonical.replace('\\', '/');
        logger.debug("Absolute path: " + absolute);
        return absolute;
    }

    private String getCanonicalPath(String full) { // TODO CAN BE ERROR
        return full.startsWith(root)
                ? full.substring(full.indexOf(root) + root.length() + 1).replace("\\", "/")
                : full;
    }

    private static void createNestedDirectory(Path uri) throws IOException {
        if (Files.exists(uri)) return;
        createNestedDirectory(uri.getParent());
        Files.createDirectory(uri);
    }

    private void deleteDirectory(String path) {
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
                    logger.debug("File deleted successfully: " + current.getAbsolutePath());
                } else {
                    logger.debug("File delete failed :" + current.getAbsolutePath());
                }
            }
        }

        if (storageDir.delete()) {
            logger.debug("Directory deleted successfully: " + storageDir.getAbsolutePath());
        } else {
            logger.debug("Directory delete failed :" + storageDir.getAbsolutePath());
        }
    }

    private List<String> getListOfContent(String currentDir, boolean isCanonicalNeeded) {
        String[] elements = new File(currentDir).list();
        List<String> list = new ArrayList<>();

        if (isDirectory(currentDir)) currentDir = currentDir + "/";

        if (isCanonicalNeeded) {
            list.add(getCanonicalPath(currentDir));
        } else {
            list.add(getFullPath(currentDir));
        }

        if (elements == null) {
            return list;
        }

        for (String current : elements) {
            list.addAll(getListOfContent(currentDir + current, isCanonicalNeeded));
        }

        return list;
    }

    public static void setContextPath(String contextPath) {
        FileStorage.contextPath = contextPath;
    }

    public static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf(".");
        int slashIndex = path.lastIndexOf("/");

        return dotIndex > slashIndex ? path.substring(dotIndex + 1) : "";
    }

    public void setRoot(String root) {
        root = root.startsWith("/") ? root : "/" + root;
        root = (contextPath + GlobalConstants.STORAGES_LOCATION + root)
                .replace('\\', '/').replace("//", "/");
        if (!Files.exists(Paths.get(root))) {
            try {
                createNestedDirectory(Paths.get(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.debug("File storage assigned to: " + Paths.get(root).toAbsolutePath());
        this.root = root;
    }
}

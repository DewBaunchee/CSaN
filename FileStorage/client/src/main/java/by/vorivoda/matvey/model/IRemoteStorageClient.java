package by.vorivoda.matvey.model;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

@Component
public interface IRemoteStorageClient {

    Map<String, String> registration(String username, String password) throws IOException;

    boolean signIn(String username, String password);

    void open(String path) throws IOException;

    void refreshAllPaths() throws IOException;

    BigInteger sizeOf(String path);

    void uploadFile(String path, File file) throws IOException;

    void uploadFile(String path, String content);

    void uploadFile(String path, byte[] content);

    void appendFile(String path, File file) throws IOException;

    void appendFile(String path, String  content);

    void appendFile(String path, byte[] content);

    void createFolder(String path);

    void move(String src, String dest);

    void copy(String src, String dest);

    void delete(String path);

    StorageStateBindings getState();

    String getStoragePath(String name);
}
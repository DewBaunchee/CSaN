package by.vorivoda.matvey.model;

import by.vorivoda.matvey.model.security.SecurityToken;
import by.vorivoda.matvey.model.security.User;
import by.vorivoda.matvey.model.util.FileInfo;
import by.vorivoda.matvey.model.util.FilesMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component("StorageClient")
public class RemoteStorageClient implements IRemoteStorageClient {

    private final Logger logger = LoggerFactory.getLogger(RemoteStorageClient.class);
    private final RestTemplate requester;
    private final String serverAddress;

    private User currentUser;
    private SecurityToken currentToken;
    private final StorageStateBindings state;

    private final List<String> temporaryFiles;
    private @Value("${maximum.temp.files}")
    int maxTempFiles;

    @Autowired
    public RemoteStorageClient(RestTemplate requester,
                               @Value("${server.address}") String serverAddress) {
        this.requester = requester;
        this.serverAddress = serverAddress;
        temporaryFiles = new ArrayList<>();
        state = new StorageStateBindings();

        state.currentFolderProperty().addListener((observable, oldValue, newValue) -> {
            try {
                open(newValue);

                if (newValue.equals("/")) return;
                if (newValue.endsWith("/")) newValue = newValue.substring(0, newValue.length() - 1);
                if (!isDirectory(newValue)) {
                    String parent = newValue.substring(0, newValue.lastIndexOf("/"));
                    parent = parent.length() == 1 ? parent : parent.substring(0, parent.length() - 1);
                    state.setCurrentFolder(parent);
                }
            } catch (Exception e) {
                state.setCurrentFolder(oldValue);
            }
        });
    }

    private boolean isDirectory(String path) {
        if (path.equals("/")) return true;
        for (String element : state.getAllPaths()) {
            if (element.equals(path + "/")) return true;
        }
        return false;
    }

    @Override
    public Map<String, String> registration(String username, String password) throws IOException {
        logger.info("Attempting to register user with username \"" + username + "\"...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("username", username);
        parameters.add("password", encode(password));

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
            ResponseEntity<String> response = requester.postForEntity(serverAddress + "/registration", request, String.class);
            logger.debug(response.toString());

            String errorReport = response.getBody();
            Map<String, String> errors = parseErrorReport(errorReport);

            if (errors.size() > 0) logger.error("Errors occurred: " + errorReport);
            else logger.info("Registered.");

            return errors;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private static Map<String, String> parseErrorReport(String errorReport) {
        if (errorReport == null || errorReport.length() == 0) return new HashMap<>();

        errorReport = errorReport.toLowerCase(Locale.ROOT);
        Scanner scanner = new Scanner(errorReport);

        Map<String, String> errors = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String key = line.substring(0, line.indexOf(":"));
            String value = line.substring(line.indexOf(":") + 1).trim();

            errors.merge(key, value, (currentErrors, newError) -> currentErrors + "; " + newError);
        }

        return errors;
    }

    private String encode(String password) {
        logger.info("Encoding password...");
        char[] encoded = new char[password.length()];

        for (int i = 0; i < encoded.length; i++) {
            encoded[i] = (char) ((password.charAt(i) * (i + 1)) % 31);
        }

        logger.info("Password " + password + " encoded to " + String.valueOf(encoded));
        return String.valueOf(encoded);
    }

    @Override
    public boolean signIn(String username, String password) {
        logger.info("Attempting to sign in user with username \"" + username + "\"...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, encode(password));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        logger.info(request.toString());
        try {
            ResponseEntity<String> response = requester.postForEntity(serverAddress + "/login", request, String.class);
            logger.debug(response.toString());

            if (response.getBody() == null) return false;
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Signed in.");
                String[] tokenFields = response.getBody().split(" ", 2);
                currentToken = new SecurityToken(tokenFields[1], new Date(Long.parseLong(tokenFields[0])));
                currentUser = new User(username, password);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void open(String path) throws IOException {
        preOperation();
        logger.info("Opening resource: " + path + "...");

        HttpHeaders headers = getDefaultHeaders();

        ResponseEntity<String> response = exchange(getHttpUrl(path), HttpMethod.GET, headers);

        MediaType responseContentType = response.getHeaders().getContentType();
        if (responseContentType != null && responseContentType.equals(MediaType.APPLICATION_JSON)) {
            ObjectMapper mapper = new ObjectMapper();
            String[] folderElements = mapper.readValue(response.getBody(), String[].class);
            state.setCurrentFolderElements(Arrays.asList(folderElements));
            state.currentFolderProperty().set(path);
        } else {
            BigInteger size = BigInteger.ZERO;

            Path temporaryFile = createTemporaryFile(path);
            try {
                if (response.getBody() != null) {
                    byte[] bytes = Base64.getDecoder().decode(response.getBody());
                    Files.write(temporaryFile, bytes);
                    size = BigInteger.valueOf(bytes.length);
                }
            } catch (IOException e) {
                Files.delete(temporaryFile);
                throw e;
            }

            addTemporaryFile(temporaryFile.toAbsolutePath().toString());
            state.setCurrentFile(path, new FileInfo(temporaryFile.toAbsolutePath().toString(), path, size));
        }
        logger.info("Opened.");
    }

    @Override
    public void refreshAllPaths() throws IOException {
        preOperation();
        logger.info("Refreshing storage...");

        HttpHeaders headers = getDefaultHeaders();
        headers.add("Operation-Type", "GET_ALL");
        ResponseEntity<String> response = exchange(getHttpUrl("/"), HttpMethod.GET, headers);
        ObjectMapper mapper = new ObjectMapper();
        String[] folderElements = mapper.readValue(response.getBody(), String[].class);
        state.setAllPaths(Arrays.asList(folderElements));
        logger.info("Refreshed.");
    }

    private void addTemporaryFile(String path) {
        while (temporaryFiles.size() > maxTempFiles - 1) {
            try {
                Files.delete(Paths.get(temporaryFiles.get(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            temporaryFiles.remove(0);
        }

        temporaryFiles.add(path);
    }

    private Path createTemporaryFile(String path) throws IOException {
        return Files.createTempFile(FilesMethods.getFileName(path), FilesMethods.getFileExtension(path));
    }

    @Override
    public BigInteger sizeOf(String path) {
        preOperation();
        logger.info("Getting size of resource: " + path + "...");

        HttpHeaders headers = getDefaultHeaders();
        headers.add("Operation-Type", "SIZE");

        ResponseEntity<String> response = exchange(getHttpUrl(path), HttpMethod.GET, headers);

        logger.info("Got: " + response.getBody());
        return response.getBody() == null ? null : new BigInteger(response.getBody());
    }

    private ResponseEntity<String> exchange(String url, HttpMethod method, HttpHeaders headers) {
        return exchange(url, method, headers, "");
    }

    private ResponseEntity<String> exchange(String url, HttpMethod method, HttpHeaders headers, String body) {
        RequestEntity<String> request = new RequestEntity<>("=" + body, headers, method, URI.create(url));
        logger.info(request.toString());
        ResponseEntity<String> response = requester.exchange(request, String.class);
        logger.debug(response.toString());
        return response;
    }

    @Override
    public void uploadFile(String path, File file) throws IOException {
        uploadFile(path, Files.readAllBytes(file.toPath()));
    }

    @Override
    public void uploadFile(String path, byte[] content) {
        uploadFile(path, Base64.getEncoder().encodeToString(content));
    }

    @Override
    public void uploadFile(String path, String content) {
        preOperation();
        logger.info("Uploading file on path: " + path + "...");
        HttpHeaders headers = getDefaultHeaders();
        exchange(getHttpUrl(path), HttpMethod.PUT, headers, content);
        logger.info("Uploaded.");
        postOperation();
    }

    @Override
    public void appendFile(String path, File file) throws IOException {
        appendFile(path, Files.readAllBytes(file.toPath()));
    }

    @Override
    public void appendFile(String path, byte[] content) {
        appendFile(path, Base64.getEncoder().encodeToString(content));
    }

    @Override
    public void appendFile(String path, String content) {
        preOperation();
        logger.info("Appending file on path: " + path + "...");
        HttpHeaders headers = getDefaultHeaders();
        if (content != null && content.length() > 0)
            exchange(getHttpUrl(path), HttpMethod.POST, headers, content);
        logger.info("Appended.");
        postOperation();
    }

    @Override
    public void createFolder(String path) {
        preOperation();
        path = getValidateDestination(path);
        logger.info("Creating folder on path: " + path + "...");
        HttpHeaders headers = getDefaultHeaders();
        headers.add("Operation-Type", "CREATE_FOLDER");
        exchange(getHttpUrl(path), HttpMethod.PUT, headers);
        logger.info("Created.");
        postOperation();
    }

    @Override
    public void move(String src, String dest) {
        preOperation();
        dest = getValidateDestination(dest);
        logger.info("Moving element from " + src + " to " + dest + "...");
        HttpHeaders headers = getDefaultHeaders();
        headers.add("Additional", dest);
        headers.add("Operation-Type", "MOVE");
        exchange(getHttpUrl(src), HttpMethod.GET, headers);
        logger.info("Moved.");
        postOperation();
    }

    @Override
    public void copy(String src, String dest) {
        preOperation();
        dest = getValidateDestination(dest);
        logger.info("Copying element from " + src + " to " + dest + "...");
        HttpHeaders headers = getDefaultHeaders();
        headers.add("Additional", dest);
        headers.add("Operation-Type", "COPY");
        exchange(getHttpUrl(src), HttpMethod.GET, headers);
        logger.info("Copied.");
        postOperation();
    }

    @Override
    public void delete(String path) {
        preOperation();
        logger.info("Deleting element on path: " + path + "...");
        HttpHeaders headers = getDefaultHeaders();
        exchange(getHttpUrl(path), HttpMethod.DELETE, headers);
        logger.info("Delete.");
        postOperation();
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentToken.getValue());
        return headers;
    }

    private void preOperation() {
        final long timeGapToExpired = 20000;
        if(currentToken == null
                || new Date().getTime() + timeGapToExpired > currentToken.getExpiredDate().getTime())
            refreshToken();
    }

    private void refreshToken() {
        signIn(currentUser.getUsername(), currentUser.getPassword());
    }

    private void postOperation() {
        logger.info("Finalizing...");
        try {
            open(state.getCurrentFolder());
            refreshAllPaths();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Finalized.");
    }

    @Qualifier("StorageState")
    @Bean
    public StorageStateBindings getState() {
        return state;
    }

    @Override
    public String getStoragePath(String name) {
        return state.getCurrentFolder().length() == 1 ? "/" + name : state.getCurrentFolder() + "/" + name;
    }

    private String getHttpUrl(String path) {
        StringBuilder encodedPath = new StringBuilder();
        int prevSlash = 0;
        for (int i = 1; i < path.length() + 1; i++) {
            if (i == path.length() || path.charAt(i) == '/') {
                encodedPath.append("/").append(URLEncoder.encode(path.substring(prevSlash + 1, i), StandardCharsets.UTF_8));
                prevSlash = i;
            }
        }

        return serverAddress + "/storage" + encodedPath;
    }

    private String getValidateDestination(String dest) {
        int dotIndex = dest.lastIndexOf(".");
        int slashIndex = dest.lastIndexOf("/");
        int suffixInsertIndex = dotIndex;

        if (dotIndex < slashIndex) {
            suffixInsertIndex = slashIndex == dest.length() ? slashIndex : dest.length();
        }

        String validatedDest = dest;
        int currentIteration = 0;
        while (state.getAllPaths().contains(validatedDest) || state.getAllPaths().contains(validatedDest + "/")) {
            validatedDest = dest.substring(0, suffixInsertIndex) +
                    "(" + currentIteration++ + ")" + dest.substring(suffixInsertIndex);
        }
        return validatedDest;
    }
}
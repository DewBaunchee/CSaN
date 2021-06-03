package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.controller.config.RequestUrl;
import by.vorivoda.matvey.controller.util.APIOperation;
import by.vorivoda.matvey.controller.util.FileStorageAPIContentOperation;
import by.vorivoda.matvey.controller.util.FileStorageAPIOperation;
import by.vorivoda.matvey.model.GlobalConstants;
import by.vorivoda.matvey.model.dao.entity.User;
import by.vorivoda.matvey.model.manager.FileStorage;
import by.vorivoda.matvey.model.manager.FileStorageFactory;
import by.vorivoda.matvey.model.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping(value = {GlobalConstants.STORAGE_PATH + "/**"
})
public class StorageController {

    private final Logger logger = LoggerFactory.getLogger(StorageController.class);
    private final FileStorageFactory fileStorageFactory;
    private final UserService userService;

    private final Map<APIOperation, FileStorageAPIOperation> operations = new HashMap<>() {{

        put(APIOperation.GET, (storage, path, ignored) -> { // Get folder or file content
            logger.info("Executing \"GET\" APIOperation...");

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            ResponseEntity<String> response;
            if (storage.isDirectory(path)) {
                headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

                ObjectMapper mapper = new ObjectMapper();
                response = new ResponseEntity<>(mapper.writeValueAsString(storage.getFolderContent(path)), headers, HttpStatus.OK);
            } else {
                headers.add("Content-Type",
                        GlobalConstants.CONTENT_TYPES.getOrDefault(
                                FileStorage.getFileExtension(path),
                                GlobalConstants.DEFAULT_CONTENT_TYPE));

                response = new ResponseEntity<>(Base64.getEncoder().encodeToString(storage.getFile(path)), headers, HttpStatus.OK);
            }
            logger.info("Executed successfully.");

            return response;
        });

        put(APIOperation.GET_ALL, (storage, path, ignoredAdditional) -> { // Get size
            logger.info("Executing \"SIZE\" APIOperation...");
            String result = new ObjectMapper().writeValueAsString(storage.getAllPaths(path));
            logger.info("Executed successfully.");
            return new ResponseEntity<>(result, HttpStatus.OK);
        });

        put(APIOperation.SIZE, (storage, path, ignored) -> { // Get size
            logger.info("Executing \"SIZE\" APIOperation...");
            BigInteger size = storage.sizeOf(path);
            logger.info("Executed successfully.");
            return new ResponseEntity<>(size.toString(), HttpStatus.OK);
        });

        put(APIOperation.COPY, (storage, src, dest) -> { // Copy
            logger.info("Executing \"COPY\" APIOperation...");
            storage.copy(src, dest);
            logger.info("Executed successfully.");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        });

        put(APIOperation.MOVE, (storage, src, dest) -> { // Move
            logger.info("Executing \"MOVE\" APIOperation...");
            storage.move(src, dest);
            logger.info("Executed successfully.");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        });

        put(APIOperation.DELETE, (storage, path, ignored) -> { // Delete
            logger.info("Executing \"DELETE\" APIOperation...");
            storage.delete(path);
            logger.info("Executed successfully.");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        });
    }};

    private final Map<APIOperation, FileStorageAPIContentOperation> contentOperations = new HashMap<>() {{

        put(APIOperation.PUT, (storage, path, content) -> { // Put or create and put file
            logger.info("Executing \"PUT\" APIOperation...");
            storage.writeFile(path, content);
            logger.info("Executed successfully.");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        });

        put(APIOperation.POST, (storage, path, content) -> { // Post file
            logger.info("Executing \"POST\" APIOperation...");
            storage.appendFile(path, content);
            logger.info("Executed successfully.");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        });

        put(APIOperation.CREATE_FOLDER, (storage, path, ignored) -> { // Create folder
            logger.info("Executing \"CREATE_FOLDER\" APIOperation...");
            storage.createFolder(path);
            logger.info("Executed successfully.");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        });
    }};

    @Autowired
    public StorageController(UserService userService,
                             FileStorageFactory fileStorageFactory,
                             ServletContext context) {
        this.userService = userService;
        this.fileStorageFactory = fileStorageFactory;
        FileStorage.setContextPath(context.getRealPath(""));
    }

    @GetMapping
    public ResponseEntity<String> getRequest(@RequestHeader(value = "Operation-Type", required = false) String operationType,
                                             @RequestHeader(value = "Additional", required = false) String additionalPath,
                                             @RequestUrl String url) throws HttpRequestMethodNotSupportedException, IOException {
        String path = getStoragePath(url);
        FileStorage storage = getAuthenticatedStorage();
        FileStorageAPIOperation operation = getAPIOperation("GET", operationType);

        if (operation == null)
            throw new HttpRequestMethodNotSupportedException("Operation type " +
                    (operationType == null ? "" : operationType) + " (GET) is not supported.");

        return operation.operate(storage, path, additionalPath);
    }

    private String getStoragePath(String url) {
        String path= url.substring(url.indexOf(GlobalConstants.STORAGE_PATH) + GlobalConstants.STORAGE_PATH.length());
        path = path.startsWith("/") ? path : "/" + path;

        StringBuilder decodedPath = new StringBuilder();
        int prevSlash = 0;
        for (int i = 1; i < path.length() + 1; i++) {
            if (i ==path.length() || path.charAt(i) == '/') {
                decodedPath.append("/").append(URLDecoder.decode(path.substring(prevSlash + 1, i), StandardCharsets.UTF_8));
                prevSlash = i;
            }
        }

        return decodedPath.toString();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRequest(@RequestUrl String url) throws HttpRequestMethodNotSupportedException, IOException {
        String path = getStoragePath(url);
        FileStorage storage = getAuthenticatedStorage();
        FileStorageAPIOperation operation = getAPIOperation("DELETE", null);

        if (operation == null)
            throw new HttpRequestMethodNotSupportedException("Operation type (DELETE) is not supported.");

        return operation.operate(storage, path, null);
    }

    @PostMapping
    public ResponseEntity<String> postRequest(@RequestHeader(value = "Operation-Type", required = false) String operationType,
                                              @RequestUrl String url,
                                              @RequestBody String body) throws HttpRequestMethodNotSupportedException, IOException {

        String path = getStoragePath(url);
        byte[] value = Base64.getDecoder().decode(body.substring(1));
        FileStorage storage = getAuthenticatedStorage();
        FileStorageAPIContentOperation operation = getAPIContentOperation("POST", operationType);

        if (operation == null)
            throw new HttpRequestMethodNotSupportedException("Operation type " + operationType + " (POST) is not supported.");

        return operation.operate(storage, path, value);
    }

    @PutMapping
    public ResponseEntity<String> putRequest(@RequestHeader(value = "Operation-Type", required = false) String operationType,
                                             @RequestUrl String url,
                                             @RequestBody String body) throws HttpRequestMethodNotSupportedException, IOException {

        String path = getStoragePath(url);
        byte[] value = Base64.getDecoder().decode(body.substring(1));
        FileStorage storage = getAuthenticatedStorage();
        FileStorageAPIContentOperation operation = getAPIContentOperation("PUT", operationType);

        if (operation == null)
            throw new HttpRequestMethodNotSupportedException("Operation type " + operationType + " (PUT) is not supported.");

        return operation.operate(storage, path, value);
    }

    private FileStorageAPIOperation getAPIOperation(String httpMethod, String paramOperation) {
        APIOperation operation = APIOperation.valueOf(paramOperation == null ?
                httpMethod : paramOperation.toUpperCase(Locale.ROOT));
        return operations.get(operation);
    }

    private FileStorageAPIContentOperation getAPIContentOperation(String httpMethod, String paramOperation) {
        APIOperation operation = APIOperation.valueOf(paramOperation == null ?
                httpMethod : paramOperation.toUpperCase(Locale.ROOT));
        return contentOperations.get(operation);
    }

    private FileStorage getAuthenticatedStorage() {
        logger.info("Trying to get file storage of authenticated...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(authentication.getName());
        if (user == null) {
            throw new UsernameNotFoundException("No user with username " + authentication.getName());
        }
        return fileStorageFactory.getFileStorage(user.getStorageInfo());
    }
}

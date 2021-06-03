package by.vorivoda.matvey.controller.util;

import by.vorivoda.matvey.model.manager.FileStorage;
import by.vorivoda.matvey.model.manager.exception.NotADirectory;
import by.vorivoda.matvey.model.manager.exception.NotAFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface FileStorageAPIOperation {
    ResponseEntity<String> operate(FileStorage targetStorage, String primaryPath, String secondaryPath) throws IOException;
}

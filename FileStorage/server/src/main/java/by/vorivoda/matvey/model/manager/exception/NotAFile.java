package by.vorivoda.matvey.model.manager.exception;

import java.io.IOException;

public class NotAFile extends IOException {
    public NotAFile(String message) {
        super(message);
    }
}

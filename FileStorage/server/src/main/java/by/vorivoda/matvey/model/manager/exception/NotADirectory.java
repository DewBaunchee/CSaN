package by.vorivoda.matvey.model.manager.exception;

import java.io.IOException;

public class NotADirectory extends IOException {
    public NotADirectory(String message) {
        super(message);
    }
}

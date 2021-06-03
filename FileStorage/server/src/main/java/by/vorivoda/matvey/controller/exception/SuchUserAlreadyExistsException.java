package by.vorivoda.matvey.controller.exception;

public class SuchUserAlreadyExistsException extends Exception{
    public SuchUserAlreadyExistsException(String message) {
        super(message);
    }
}

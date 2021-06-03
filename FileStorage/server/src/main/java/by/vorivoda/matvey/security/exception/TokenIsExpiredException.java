package by.vorivoda.matvey.security.exception;

public class TokenIsExpiredException extends Exception{
    public TokenIsExpiredException(String message) {
        super(message);
    }
}

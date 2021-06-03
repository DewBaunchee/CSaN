package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.controller.exception.InvalidRegistrationArgumentsException;
import by.vorivoda.matvey.controller.exception.SuchUserAlreadyExistsException;
import by.vorivoda.matvey.model.manager.exception.NotADirectory;
import by.vorivoda.matvey.model.manager.exception.NotAFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            AuthenticationServiceException.class,
            SuchUserAlreadyExistsException.class,
            InvalidRegistrationArgumentsException.class})
    public ResponseEntity<String> handleAuthorizationExceptions(Exception e) {
        logError(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {
            NotADirectory.class,
            NoSuchFileException.class,
            NotAFile.class,
            NoSuchElementException.class})
    public ResponseEntity<String> handleBadRequestExceptions(Exception e) {
        logError(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<String> handleNotImplementedExceptions(Exception e) {
        logError(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<String> handlerInternalExceptions(Exception e) {
        logError(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception e) {
        logger.error(e.getClass() + ": " + e.getMessage());
    }
}

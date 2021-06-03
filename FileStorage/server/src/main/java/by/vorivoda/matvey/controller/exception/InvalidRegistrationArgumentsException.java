package by.vorivoda.matvey.controller.exception;

public class InvalidRegistrationArgumentsException extends Exception{
    public InvalidRegistrationArgumentsException(String errorsReport) {
        super(errorsReport);
    }
}

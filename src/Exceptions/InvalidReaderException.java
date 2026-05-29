package Exceptions;

public class InvalidReaderException extends RuntimeException{
    public InvalidReaderException(String message) {
        super(message);
    }
}
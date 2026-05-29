package Exceptions;

public class InvalidBookCopyException extends RuntimeException{
    public InvalidBookCopyException(String message) {
        super(message);
    }
}

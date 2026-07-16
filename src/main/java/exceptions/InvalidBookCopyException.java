package exceptions;

public class InvalidBookCopyException extends RuntimeException {
    public InvalidBookCopyException(String message) {
        super(message);
    }
}

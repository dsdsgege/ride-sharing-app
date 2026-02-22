package hu.ridesharing.exception;

public class EmailSendingError extends RuntimeException {
    public EmailSendingError(String message) {
        super(message);
    }
}

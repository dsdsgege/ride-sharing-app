package hu.ridesharing.exception;

public class JoinRideException extends RuntimeException {
    public JoinRideException(String message) {
        super(message);
    }

    public JoinRideException(String message, Throwable cause) {
        super(message, cause);
    }
}

package hu.ridesharing.exception;

public class DriveNotFoundException extends RuntimeException {
    public DriveNotFoundException(String message) {
        super(message);
    }
}

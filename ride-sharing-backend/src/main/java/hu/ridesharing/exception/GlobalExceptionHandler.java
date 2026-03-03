package hu.ridesharing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDriverNotFoundException(DriverNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DriveNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDriveNotFoundException(DriveNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<Map<String, String>> handleChatException(ChatException ex) {
        return ResponseEntity.internalServerError().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(JoinRideException.class)
    public ResponseEntity<Map<String, String>> handleJoinRideException(JoinRideException ex) {
        return ResponseEntity.internalServerError().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(EmailSendingError.class)
    public ResponseEntity<Map<String, String>> handleEmailSendingError(EmailSendingError ex) {
        return ResponseEntity.internalServerError().body(Map.of("message", ex.getMessage()));
    }
}

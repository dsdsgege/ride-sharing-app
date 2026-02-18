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
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", errorMessage));
    }

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<Map<String, String>> handleChatException(ChatException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.internalServerError().body(Map.of("error", errorMessage));
    }
}

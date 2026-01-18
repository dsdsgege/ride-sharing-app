package hu.ridesharing.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDriverNotFoundException(DriverNotFoundException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
    }
}

package com.aeropuerto.airport.presentation.error;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ApiException.class)
  ResponseEntity<Map<String, Object>> api(ApiException ex) {
    return ResponseEntity.status(ex.status()).body(body(ex.status(), ex.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
    String fields = ex.getBindingResult().getFieldErrors().stream()
        .map(this::fieldMessage).collect(Collectors.joining("; "));
    return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, fields));
  }
  @ExceptionHandler(Exception.class)
  ResponseEntity<Map<String, Object>> generic(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno"));
  }
  private String fieldMessage(FieldError e) { return e.getField() + " " + e.getDefaultMessage(); }
  private Map<String, Object> body(HttpStatus status, String message) {
    return Map.of("timestamp", Instant.now().toString(), "status", status.value(), "error", status.getReasonPhrase(), "message", message);
  }
}

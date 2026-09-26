package dev.learn.tinylinks;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidUrlException.class)
    ResponseEntity<Map<String, String>> invalidUrl() {
        return ResponseEntity.badRequest().body(Map.of("error", "provide_a_valid_http_or_https_url"));
    }

    @ExceptionHandler(LinkNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "link_not_found"));
    }
}

class InvalidUrlException extends RuntimeException {}
class LinkNotFoundException extends RuntimeException {}

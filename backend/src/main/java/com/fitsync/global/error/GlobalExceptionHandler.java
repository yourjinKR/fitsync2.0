package com.fitsync.global.error;

import com.fitsync.global.error.exception.BadRequestException;
import com.fitsync.global.error.exception.ResourceConflictException;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import com.fitsync.global.error.exception.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 처리
public class GlobalExceptionHandler {

    // 잘못된 요청 (400)
    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // 권한 없음 (403)
    @ExceptionHandler(UnauthorizedAccessException.class)
    protected ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    // 리소스를 찾을 수 없음 (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 자원 중복 (409)
    @ExceptionHandler(ResourceConflictException.class)
    protected ResponseEntity<String> handleResourceConflictException(ResourceConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
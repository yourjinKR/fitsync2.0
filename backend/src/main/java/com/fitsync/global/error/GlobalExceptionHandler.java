package com.fitsync.global.error;

import com.fitsync.global.error.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 처리
public class GlobalExceptionHandler {

    // ResourceNotFoundException 타입의 예외가 발생하면 이 메소드가 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        // 예외 메시지를 body에 담아 404 NOT_FOUND 상태 코드로 응답
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
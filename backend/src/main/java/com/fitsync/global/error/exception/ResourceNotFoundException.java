package com.fitsync.global.error.exception;

// 해당 리소스를 찾을 수 없을 때 던질 예외
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

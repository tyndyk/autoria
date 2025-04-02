package com.example.auto_ria.exceptions.auth;

import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends RuntimeException {
    private HttpStatus status;

    public PermissionDeniedException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

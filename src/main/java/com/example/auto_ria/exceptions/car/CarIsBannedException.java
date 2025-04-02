package com.example.auto_ria.exceptions.car;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
public class CarIsBannedException extends RuntimeException {
    public CarIsBannedException(String message) {
        super(message);
    }
}
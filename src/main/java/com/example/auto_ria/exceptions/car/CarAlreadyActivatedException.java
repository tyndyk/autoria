package com.example.auto_ria.exceptions.car;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
public class CarAlreadyActivatedException extends RuntimeException {
    public CarAlreadyActivatedException(String message) {
        super(message);
    }
}

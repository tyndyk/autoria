package com.example.auto_ria.exceptions.verification;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProfanityFoundException extends RuntimeException {
    public ProfanityFoundException(String message) {
        super(message);
    }
}
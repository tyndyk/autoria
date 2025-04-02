package com.example.auto_ria.controllers.errors;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, String>> handleError() {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "The requested resource was not found.");
        errorResponse.put("status", "404");

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}

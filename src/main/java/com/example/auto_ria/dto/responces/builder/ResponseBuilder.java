package com.example.auto_ria.dto.responces.builder;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.auto_ria.dto.responces.PaginatedResponse;
import com.example.auto_ria.dto.responces.ResponceObj;

public class ResponseBuilder {

    public static <T> ResponseEntity<ResponceObj<T>> buildResponse(HttpStatus status, String message, T data) {
        ResponceObj<T> response = new ResponceObj<>(status.value(), message, data);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ResponceObj<T>> buildResponse(T data) {
        ResponceObj<T> response = new ResponceObj<>(HttpStatus.OK.value(), "SUCCESS", data);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static <T> ResponseEntity<ResponceObj<T>> buildResponse(String message) {
        ResponceObj<T> response = new ResponceObj<>(HttpStatus.OK.value(), message, null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static <T> ResponseEntity<ResponceObj<PaginatedResponse<T>>> buildPagedResponse(Page<T> page) {

        PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize());

        ResponceObj<PaginatedResponse<T>> response = new ResponceObj<>(HttpStatus.OK.value(), "SUCCESS",
                paginatedResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

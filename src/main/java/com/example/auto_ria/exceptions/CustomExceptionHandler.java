package com.example.auto_ria.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.auto_ria.dto.responces.ApiError;
import com.example.auto_ria.exceptions.auth.InvalidCredentialsException;
import com.example.auto_ria.exceptions.auth.PermissionDeniedException;
import com.example.auto_ria.exceptions.auth.UnauthorizedAccessException;
import com.example.auto_ria.exceptions.email.EmailSendFailedException;
import com.example.auto_ria.exceptions.file.FileTransferException;
import com.example.auto_ria.exceptions.file.FileValidationException;
import com.example.auto_ria.exceptions.general.DatabaseOperationException;
import com.example.auto_ria.exceptions.general.InternalServerException;
import com.example.auto_ria.exceptions.password.InvalidPasswordException;
import com.example.auto_ria.exceptions.token.InvalidTokenException;
import com.example.auto_ria.exceptions.token.TokenExpiredException;
import com.example.auto_ria.exceptions.user.InvalidUserRoleException;
import com.example.auto_ria.exceptions.user.UserAlreadyActivatedException;
import com.example.auto_ria.exceptions.user.UserAlreadyExistsException;
import com.example.auto_ria.exceptions.user.UserNotActivatedException;
import com.example.auto_ria.exceptions.user.UserNotFoundException;
import com.example.auto_ria.exceptions.verification.InvalidCodeException;

@RestControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "User already exists", ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "User not found", ex);
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ApiError> handleUserNotActivatedException(UserNotActivatedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "User account is not activated", ex);
    }

    @ExceptionHandler(InvalidUserRoleException.class)
    public ResponseEntity<ApiError> handleInvalidUserRoleException(InvalidUserRoleException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid user role", ex);
    }

    @ExceptionHandler(UserAlreadyActivatedException.class)
    public ResponseEntity<ApiError> handleUserAlreadyActivatedException(UserAlreadyActivatedException ex) {
        return buildResponse(HttpStatus.CONFLICT, "User is already activated", ex);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials", ex);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ex);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpiredException(TokenExpiredException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token has expired", ex);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid token", ex);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordException(InvalidPasswordException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid password", ex);
    }

    @ExceptionHandler(EmailSendFailedException.class)
    public ResponseEntity<ApiError> handleEmailSendFailedException(EmailSendFailedException ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "Email sending failed", ex);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ApiError> handlePermissionDeniedException(PermissionDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Permission denied", ex);
    }

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<ApiError> handleInvalidRegistrationCodeException(InvalidCodeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid code", ex);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ApiError> handleDatabaseOperationException(DatabaseOperationException ex) {
        logger.error("Database error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "A database error occurred", "Please try again later.");
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternalServerException(InternalServerException ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "Something went wrong. Try again later." + ex.getMessage());
    }

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ApiError> handleFileValidationException(FileValidationException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading picture", ex);
    }

    @ExceptionHandler(FileTransferException.class)
    public ResponseEntity<ApiError> handleFileTransferException(FileTransferException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File transfer failed", ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid input", ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", "Not allowed to access this endpoint");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error",
                "An unexpected error occurred. Please try again later");
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String title, Exception ex) {
        logger.warn("{}: {}", title, ex.getMessage());
        ApiError errorResponse = new ApiError(status.value(), title, ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String title, String userMessage) {
        ApiError errorResponse = new ApiError(status.value(), title, userMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }
}

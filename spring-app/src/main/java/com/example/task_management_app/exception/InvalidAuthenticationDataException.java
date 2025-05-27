package com.example.task_management_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when required authentication data is missing
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAuthenticationDataException extends RuntimeException {

    public InvalidAuthenticationDataException(String message) {
        super(message);
    }

    public InvalidAuthenticationDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

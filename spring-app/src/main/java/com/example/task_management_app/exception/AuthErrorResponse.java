package com.example.task_management_app.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Specialized error response for authentication-related errors
 * Provides additional context while maintaining security
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthErrorResponse {
    private int status;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;
    private String suggestion;

    public AuthErrorResponse(int status, String message, String errorCode, LocalDateTime timestamp, String path) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
        this.path = path;
    }
}

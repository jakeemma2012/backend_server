package com.ProjectMovie.Utils;

import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class ErrorResponseBuilder {
    public static ProblemDetail buildErrorResponse(HttpStatus status, String message, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("status", "error");
        return problemDetail;
    }
} 
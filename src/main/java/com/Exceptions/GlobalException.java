package com.Exceptions;

import com.ProjectMovie.Exceptions.EmptyFileException;
import com.ProjectMovie.Exceptions.FileExistException;
import com.ProjectMovie.Exceptions.MovieNotFoundException;
import com.ProjectMovie.Exceptions.UserException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    
    @ExceptionHandler(FileExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleFileExistException(FileExistException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("File Already Exists");
        return problemDetail;
    }

    @ExceptionHandler(EmptyFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleEmptyFileException(EmptyFileException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Empty File");
        return problemDetail;
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleMovieNotFoundException(MovieNotFoundException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Movie Not Found");
        return problemDetail;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("User Already Exists");
        return problemDetail;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleInvalidCredentialsException(InvalidCredentialsException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Invalid Credentials");
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAuthenticationException(AuthenticationException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Authentication Failed");
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Access Denied");
        return problemDetail;
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleLockedException(LockedException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Account Locked");
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGlobalException(Exception e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleUserException(UserException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("User Exception");
        return problemDetail;
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    @ResponseStatus(HttpStatus.OK)
    public static ProblemDetail handleJwtAuthenticationException(JwtAuthenticationException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.OK, e.getMessage());
        problemDetail.setTitle("Invalid Token");
        problemDetail.setStatus(403);
        return problemDetail;
    }


    public static void sendErrorResponse(HttpServletResponse response,String title, String detail) throws IOException, StreamWriteException, java.io.IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("type", "about:blank");
        errorResponse.put("title", title);
        errorResponse.put("detail", detail);

        objectMapper.writeValue(response.getWriter(), errorResponse);
        return;
    }

}
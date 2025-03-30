package com.ProjectMovie.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(EmptyFileException.class)
    public ProblemDetail handleEmptyFileException(EmptyFileException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ProblemDetail handleMovieNotFoundException(MovieNotFoundException e){
       return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(FileExistException.class)
    public ProblemDetail handleFileExistException(FileExistException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    
}

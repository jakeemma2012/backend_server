package com.ProjectMovie.Exceptions;

public class FileExistException extends RuntimeException {
    public FileExistException(String message) {
        super(message);
    }
}

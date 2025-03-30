package com.ProjectMovie.Exceptions;

public class RefeshTokenExpried extends RuntimeException {
    public RefeshTokenExpried(String message){
        super(message);
    }
}

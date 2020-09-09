package com.lk.taskmanager.utils.exceptions;

public class UnAuthorizedAccessException extends RuntimeException {

    public UnAuthorizedAccessException() { }

    public UnAuthorizedAccessException(String message) {
        super(message);
    }

    public UnAuthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorizedAccessException(Throwable cause) {
        super(cause);
    }
}
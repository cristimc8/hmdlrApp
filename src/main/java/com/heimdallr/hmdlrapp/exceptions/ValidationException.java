package com.heimdallr.hmdlrapp.exceptions;

public class ValidationException extends Exception {
    public ValidationException(String errorMessage) {
        super(errorMessage);
    }
}

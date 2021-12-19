package com.heimdallr.hmdlrapp.exceptions;

public class NotFoundException extends Exception {
    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

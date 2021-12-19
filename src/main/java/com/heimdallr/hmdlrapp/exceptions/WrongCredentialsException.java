package com.heimdallr.hmdlrapp.exceptions;

public class WrongCredentialsException extends Exception {
    public WrongCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}

package com.heimdallr.hmdlrapp.exceptions;

public class WrongInputException extends Exception {
    public WrongInputException(String errorMessage) {
        super(errorMessage);
    }
}

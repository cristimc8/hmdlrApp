package com.heimdallr.hmdlrapp.exceptions;

/**
 * Custom exception class used when we don't want to allow a non-service class
 * to be instantiated as a singleton Service.
 */
public class ServiceInitException extends Exception {
    public ServiceInitException(String errorMessage) {
        super(errorMessage);
    }
}

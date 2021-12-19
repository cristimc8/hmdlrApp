package com.heimdallr.hmdlrapp.exceptions;

/**
 * Custom exception class used when a user tries to get a non-registered service or class
 * from a container instance.
 */
public class ServiceNotRegisteredException extends Exception {
    public ServiceNotRegisteredException(String errorMessage) {
        super(errorMessage);
    }
}

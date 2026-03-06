package com.innowise.authservice.exeption;

public class RegistrationOrchestrationException extends RuntimeException {
    public RegistrationOrchestrationException(String message) {
        super(message);
    }

    public RegistrationOrchestrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

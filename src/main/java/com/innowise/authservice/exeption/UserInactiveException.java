package com.innowise.authservice.exeption;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException() {
        super("User is inactive");
    }
}

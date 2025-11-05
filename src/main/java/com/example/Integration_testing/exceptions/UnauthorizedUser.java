package com.example.Integration_testing.exceptions;

public class UnauthorizedUser extends RuntimeException {
    public UnauthorizedUser(String message) {
        super(message);
    }
}
package com.example.demo.infrastructure.exception;

public class AuthCustomException extends RuntimeException {
    public AuthCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}


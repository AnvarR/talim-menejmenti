package com.edu.talim.exception;

// Login/parol noto'g'ri — HTTP 401
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
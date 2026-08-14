package com.edu.talim.exception;

// Juda ko'p noto'g'ri urinish qilingan (login/parol o'zgartirish) - HTTP 429
public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException(String message) {
        super(message);
    }
}
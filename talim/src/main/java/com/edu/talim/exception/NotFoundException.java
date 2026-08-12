package com.edu.talim.exception;

// Resurs topilmadi (masalan: "Kursant topilmadi") — HTTP 404
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
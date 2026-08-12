package com.edu.talim.exception;

// Ma'lumot allaqachon mavjud yoki band (masalan: "Bu fan oldin qo'shilgan!") — HTTP 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
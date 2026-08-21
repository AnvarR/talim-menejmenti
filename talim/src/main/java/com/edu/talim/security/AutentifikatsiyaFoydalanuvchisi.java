package com.edu.talim.security;

// JWT ichidan olingan foydalanuvchi ma'lumotlari - SecurityContext'da principal sifatida saqlanadi.
// userType - "USER" (xodim) yoki "STUDENT" (kursant/tinglovchi)
public record AutentifikatsiyaFoydalanuvchisi(Long userId, String userType, String role) {
}
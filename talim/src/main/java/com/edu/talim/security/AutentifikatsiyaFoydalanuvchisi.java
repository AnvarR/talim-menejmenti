package com.edu.talim.security;

// JWT ichidan olingan foydalanuvchi ma'lumotlari - SecurityContext'da principal sifatida saqlanadi.
// userId - matn ko'rinishida (User uchun Long, Student uchun UUID bo'lishi mumkin).
// userType - "USER" (xodim) yoki "STUDENT" (kursant/tinglovchi)
public record AutentifikatsiyaFoydalanuvchisi(String userId, String userType, String role) {
}

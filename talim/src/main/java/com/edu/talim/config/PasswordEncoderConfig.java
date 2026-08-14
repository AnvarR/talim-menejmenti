package com.edu.talim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Diqqat: bu yerda faqat parol hashlash uchun kerakli bean bor.
// To'liq Spring Security (filter zanjiri, autentifikatsiya) hali ULANMAGAN -
// bu keyingi, alohida bosqichda (barcha modul tayyor bo'lgach) qo'shiladi.
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
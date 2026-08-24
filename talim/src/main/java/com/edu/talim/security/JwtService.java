package com.edu.talim.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT (JSON Web Token) yaratish va tekshirish. Token ichida foydalanuvchi ID'si,
// turi (USER/STUDENT) va roli saqlanadi - shu bilan server "sessiya" saqlamasdan
// (stateless) har bir so'rovda kimligini bilib oladi.
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Token yaratish - login muvaffaqiyatli o'tgandan keyin chaqiriladi.
    // userId - matn sifatida beriladi, chunki User.id (Long) va Student.id (UUID)
    // ikkalasi ham bo'lishi mumkin, ammo token ichida ikkalasi ham shunchaki matn
    public String generateToken(String userId, String userType, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("userType", userType)   // "USER" yoki "STUDENT"
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key())
                .compact();
    }

    // Tokenni tekshiradi va ichidagi ma'lumotlarni qaytaradi (agar noto'g'ri/muddati
    // o'tgan bo'lsa - JwtException otiladi, buni filter tutib oladi)
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

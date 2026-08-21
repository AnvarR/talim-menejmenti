package com.edu.talim.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Har bir HTTP so'rovda "Authorization: Bearer <token>" sarlavhasini tekshiradi.
// Agar token to'g'ri bo'lsa - SecurityContext'ga foydalanuvchi (ID + rol) yoziladi,
// shundan keyin controller/servislar "kim so'rov yuboryapti"ni bilishi mumkin bo'ladi.
// Token yo'q yoki noto'g'ri bo'lsa - shunchaki hech kim autentifikatsiya qilinmagan
// holda davom etadi (keyin SecurityConfig qaysi endpoint himoyalanganini hal qiladi).
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtService.parseToken(token);
                Long userId = Long.parseLong(claims.getSubject());
                String userType = claims.get("userType", String.class);
                String role = claims.get("role", String.class);

                AutentifikatsiyaFoydalanuvchisi principal =
                        new AutentifikatsiyaFoydalanuvchisi(userId, userType, role);

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                // Token noto'g'ri/muddati o'tgan - autentifikatsiyasiz davom etadi,
                // SecurityConfig himoyalangan endpointlar uchun 401 qaytaradi
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
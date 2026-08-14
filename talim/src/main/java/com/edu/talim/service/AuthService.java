package com.edu.talim.service;

import com.edu.talim.config.KirishUrinishLimiter;
import com.edu.talim.exception.UnauthorizedException;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.LoginRequestDTO;
import com.edu.talim.dto.LoginResponseDTO;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.User;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final KirishUrinishLimiter kirishUrinishLimiter;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        String limiterKaliti = "login:" + dto.getUsername();
        kirishUrinishLimiter.tekshir(limiterKaliti);

        // 1. Avval users jadvalidan qidiramiz
        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);

        if (user != null) {
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                kirishUrinishLimiter.muvaffaqiyatsiz(limiterKaliti);
                throw new UnauthorizedException("Parol noto'g'ri!");
            }
            kirishUrinishLimiter.muvaffaqiyatli(limiterKaliti);
            return LoginResponseDTO.builder()
                    .id(user.getId())
                    .fio(user.getFio())
                    .role(user.getRole() != null ? user.getRole().name() : null)
                    .username(user.getUsername())
                    .photoUrl(user.getPhotoUrl())
                    .build();
        }

        // 2. Topilmasa students jadvalidan qidiramiz
        Student student = studentRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    kirishUrinishLimiter.muvaffaqiyatsiz(limiterKaliti);
                    return new NotFoundException("Foydalanuvchi topilmadi!");
                });

        if (!passwordEncoder.matches(dto.getPassword(), student.getPassword())) {
            kirishUrinishLimiter.muvaffaqiyatsiz(limiterKaliti);
            throw new UnauthorizedException("Parol noto'g'ri!");
        }

        kirishUrinishLimiter.muvaffaqiyatli(limiterKaliti);
        return LoginResponseDTO.builder()
                .id(student.getId())
                .fio(student.getFio())
                .role(student.getRole() != null ? student.getRole().name() : null)
                .username(student.getUsername())
                .photoUrl(student.getPhotoUrl())
                .build();
    }
}
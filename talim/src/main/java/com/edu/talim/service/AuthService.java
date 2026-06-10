package com.edu.talim.service;

import com.edu.talim.dto.LoginRequestDTO;
import com.edu.talim.dto.LoginResponseDTO;
import com.edu.talim.entity.User;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Username bo'yicha topamiz
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi!"));

        // Parolni tekshiramiz
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Parol noto'g'ri!");
        }

        return LoginResponseDTO.builder()
                .id(user.getId())
                .fio(user.getFio())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .username(user.getUsername())
                .photoUrl(user.getPhotoUrl())
                .build();
    }
}
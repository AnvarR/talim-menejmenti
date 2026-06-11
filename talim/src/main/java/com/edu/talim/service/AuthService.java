package com.edu.talim.service;

import com.edu.talim.dto.LoginRequestDTO;
import com.edu.talim.dto.LoginResponseDTO;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.User;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public LoginResponseDTO login(LoginRequestDTO dto) {

        // 1. Avval users jadvalidan qidiramiz
        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);

        if (user != null) {
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

        // 2. Topilmasa students jadvalidan qidiramiz
        Student student = studentRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi!"));

        if (!student.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Parol noto'g'ri!");
        }

        return LoginResponseDTO.builder()
                .id(student.getId())
                .fio(student.getFio())
                .role(student.getRole() != null ? student.getRole().name() : null)
                .username(student.getUsername())
                .photoUrl(student.getPhotoUrl())
                .build();
    }
}
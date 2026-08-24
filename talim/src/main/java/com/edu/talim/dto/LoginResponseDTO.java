package com.edu.talim.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    // String - chunki User.id (Long) yoki Student.id (UUID) bo'lishi mumkin
    private String id;
    private String fio;
    private String role;
    private String username;
    private String photoUrl;
    private String token;
}